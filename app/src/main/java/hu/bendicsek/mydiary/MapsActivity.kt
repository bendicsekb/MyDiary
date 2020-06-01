package hu.bendicsek.mydiary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import hu.bendicsek.mydiary.data.AppDatabase
import hu.bendicsek.mydiary.data.DiaryEntry
import java.security.KeyStore

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var entries: List<DiaryEntry>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        getAllEntries()
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    fun getAllEntries() {
        Thread {
            entries = AppDatabase.getInstance(this@MapsActivity).diaryDao().getAllEntries()
        }.start()
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        for (entry in entries) {
            if (entry.latitude != null && entry.longitude != null) {
                val pos = LatLng(entry.latitude!!, entry.longitude!!)
                val marker = mMap.addMarker(
                    MarkerOptions()
                        .position(pos)
                        .title(entry.diaryEntryTitle)
                )
                marker.showInfoWindow()
            }
        }

        var default_pos = LatLng(47.4813, 19.0555)
        if (entries.size > 0) {
            if (entries.first().latitude != null && entries.first().longitude != null) {
                default_pos = LatLng(entries.first().latitude!!, entries.first().longitude!!)
            }
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLng(default_pos))
    }
}
