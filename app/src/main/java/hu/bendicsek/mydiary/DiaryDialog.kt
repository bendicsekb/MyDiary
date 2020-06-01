package hu.bendicsek.mydiary

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.google.android.gms.location.*
import hu.bendicsek.mydiary.data.DiaryEntry
import kotlinx.android.synthetic.main.diary_dialog.view.*
import kotlinx.android.synthetic.main.diary_dialog.view.tvLocation
import java.text.SimpleDateFormat
import java.util.*

class DiaryDialog : DialogFragment() {

    interface DiaryEntryHandler {
        fun entryCreated(todo: DiaryEntry)
    }

    lateinit var diaryHandler: DiaryEntryHandler
    lateinit var fusedLocationClient : FusedLocationProviderClient

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        if (context is DiaryEntryHandler) {
            diaryHandler = context
        } else {
            throw RuntimeException(
                "The Activity does not implement the DialogHandler interface!"
            )
        }
    }

    lateinit var etTitleText: EditText
    lateinit var etBodyText: EditText
    lateinit var etPlaceText: EditText
    lateinit var etDate: EditText
    lateinit var cbIsPersonal: CheckBox
    lateinit var btnSetPosition: Button
    lateinit var tvLocation: TextView
    var longitude: Double? = null
    var latitude: Double? = null


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogBuilder = AlertDialog.Builder(requireContext())

        dialogBuilder.setTitle("Add Diary Entry")
        val dialogView = requireActivity().layoutInflater.inflate(
            R.layout.diary_dialog, null
        )

        etTitleText = dialogView.etTitleText
        etBodyText = dialogView.etBodyText
        etPlaceText = dialogView.etPlaceText
        cbIsPersonal = dialogView.cbIsPersonal
        etDate = dialogView.dob

        etDate.hint = SimpleDateFormat("dd.MM.yyyy").format(System.currentTimeMillis())

        val cal = Calendar.getInstance()
        val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val myFormat = "dd.MM.yyyy"
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            etDate.hint = sdf.format(cal.time)

        }
            etDate.setOnClickListener {
            DatePickerDialog(
                this.context!!,
                dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        btnSetPosition = dialogView.btnSetPosition
        tvLocation = dialogView.tvLocation

        // Location
        btnSetPosition.setOnClickListener {
            locationListener()
        }
         requestNeededPermission()

        dialogBuilder.setView(dialogView)

        dialogBuilder.setNegativeButton("Cancel") {
            dialog, which ->
        }
        dialogBuilder.setPositiveButton("Add") {
            dialog, which ->
        }
        return dialogBuilder.create()
    }

    override fun onResume() {
        super.onResume()

        val positiveButton = (dialog as AlertDialog).getButton(Dialog.BUTTON_POSITIVE)
        positiveButton.setOnClickListener {
            for(etTextField in arrayOf(etTitleText, etBodyText)){
                if (etTextField.text.isEmpty()){
                    etTextField.error = "This field can not be empty"
                }
            }
            if (etTitleText.text.isNotEmpty() && etBodyText.text.isNotEmpty()) {
                handleTodoCreate()
                stopLocationMonitoring()
                (dialog as AlertDialog).dismiss()
            }
        }
    }

    private fun handleTodoCreate() {
        if (! useLocation) {
            longitude = null
            latitude = null
        }

        diaryHandler.entryCreated(
            DiaryEntry(
                null,
                etDate.hint.toString(),
                etTitleText.text.toString(),
                etBodyText.text.toString(),
                cbIsPersonal.isChecked,
                etPlaceText.text.toString(),
                longitude,
                latitude
            )
        )
    }

    /* LOCATION SERVICE */

    // PERMISSIONS
    private fun requestNeededPermission() {
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {

            if (shouldShowRequestPermissionRationale(
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                Toast.makeText(
                    activity,
                    "I need it for location", Toast.LENGTH_SHORT
                ).show()
            }

            requestPermissions(
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                101
            )
        } else {
            startLocationMonitoring()
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            101 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(activity, "ACCESS_FINE_LOCATION perm granted", Toast.LENGTH_SHORT)
                        .show()

                    startLocationMonitoring()
                } else {
                    Toast.makeText(
                        activity,
                        "ACCESS_FINE_LOCATION perm NOT granted",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
    // END OF PERMISSION

    var useLocation = false
    private fun locationListener(){
        useLocation = true
        if (prevLocation != null){
            Thread {
                try {
                    val gc = Geocoder(activity, Locale.getDefault())
                    var addrs: List<Address> =
                        gc.getFromLocation(prevLocation!!.latitude, prevLocation!!.longitude, 3)
                    val addr =
                        "${addrs[0].getAddressLine(0)}, ${addrs[0].getAddressLine(1)}," +
                                " ${addrs[0].getAddressLine(2)}"

                    activity?.runOnUiThread {
                        Toast.makeText(activity, addr, Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    activity?.runOnUiThread {
                        Toast.makeText(
                            activity,
                            "Error: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }.start()
        }
    }


    var prevLocation:Location? = null

    private var locationCallback : LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)

            prevLocation = locationResult.lastLocation
            var locationText =
                "${locationResult.lastLocation.latitude}, ${locationResult.lastLocation.longitude}"
            tvLocation.text = locationText
            longitude = locationResult.lastLocation.longitude
            latitude = locationResult.lastLocation.latitude
        }
    }

    fun startLocationMonitoring() {
        val locRequest = LocationRequest()
        locRequest.interval = 1000
        locRequest.fastestInterval = 500
        locRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        fusedLocationClient.requestLocationUpdates(locRequest,
            locationCallback, Looper.myLooper())
    }

    fun stopLocationMonitoring() {
        longitude = null
        latitude = null
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}
