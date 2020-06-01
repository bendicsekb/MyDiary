package hu.bendicsek.mydiary

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.GridLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import hu.bendicsek.mydiary.adapter.DiaryAdapter
import hu.bendicsek.mydiary.data.AppDatabase
import hu.bendicsek.mydiary.data.DiaryEntry
import hu.bendicsek.mydiary.touch.DiaryReyclerTouchCallback
import kotlinx.android.synthetic.main.activity_scrolling.*
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt

class ScrollingActivity : AppCompatActivity(), DiaryDialog.DiaryEntryHandler {

    lateinit var diaryAdapter: DiaryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scrolling)
        setSupportActionBar(toolbar)

        fab_add.setOnClickListener { view ->
            DiaryDialog().show(supportFragmentManager,
                "DIARY_DIALOG")
        }

        fab_rm.setOnClickListener { view ->
            diaryAdapter.deleteAll()
        }

        initRecyclerView()

        if (!wasStartedBefore()) {
            MaterialTapTargetPrompt.Builder(this)
                .setTarget(R.id.fab_add)
                .setPrimaryText("New diary entry")
                .setSecondaryText("Click here to add new diary entry")
                .show()
            saveWasStarted()
        }
    }

    /* MENU */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_scrolling, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_map -> {
                var intent = Intent(this@ScrollingActivity, MapsActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_delete_all -> {
                diaryAdapter.deleteAll()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        // Do nothing, that is the expected behavior on the main screen
    }

    fun saveWasStarted() {
        var sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        var editor = sharedPref.edit()
        editor.putBoolean("KEY_STARTED", true)
        editor.apply()
    }

    fun wasStartedBefore() : Boolean {
        var sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        return sharedPref.getBoolean("KEY_STARTED", false)
    }

    fun initRecyclerView() {
        Thread {
            var diaryList =
                AppDatabase.getInstance(this@ScrollingActivity).diaryDao().getAllEntries()

            runOnUiThread {
                diaryAdapter = DiaryAdapter(this, diaryList)

                //recyclerTodo.layoutManager = GridLayoutManager(this,2)
                var itemDecorator = DividerItemDecoration(this,
                    DividerItemDecoration.VERTICAL)
                recyclerDiary.addItemDecoration(itemDecorator)

                recyclerDiary.adapter = diaryAdapter

                val callback = DiaryReyclerTouchCallback(diaryAdapter)
                val touchHelper = ItemTouchHelper(callback)
                touchHelper.attachToRecyclerView(recyclerDiary)
            }

        }.start()
    }

    override fun entryCreated(entry: DiaryEntry) {
        Thread {
            var newId = AppDatabase.getInstance(this@ScrollingActivity).diaryDao().addEntry(
                entry
            )
            entry.diaryEntryId = newId
            runOnUiThread{
                diaryAdapter.addEntry(entry)
            }
        }.start()
    }

}

