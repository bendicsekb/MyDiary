package hu.bendicsek.mydiary.adapter

import android.animation.Animator
import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.RecyclerView
import hu.bendicsek.mydiary.R
import hu.bendicsek.mydiary.ScrollingActivity
import hu.bendicsek.mydiary.data.AppDatabase
import hu.bendicsek.mydiary.data.DiaryEntry
import hu.bendicsek.mydiary.touch.DiaryTouchHelperCallback
import kotlinx.android.synthetic.main.diary_entry.view.*
import java.util.*

class DiaryAdapter : RecyclerView.Adapter<DiaryAdapter.ViewHolder>, DiaryTouchHelperCallback {

    var entries = mutableListOf<DiaryEntry>()

    val context: Context

    constructor(context: Context, entryList: List<DiaryEntry>) : super() {
        this.context = context
        entries.addAll(entryList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val diaryView = LayoutInflater.from(context).inflate(
            R.layout.diary_entry, parent, false
        )
        return ViewHolder(diaryView)
    }

    override fun getItemCount(): Int {
        return entries.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entry = entries[position]

        holder.tvEntryTitle.text = entry.diaryEntryTitle
        holder.tvEntryText.text = entry.diaryEntryText
        holder.tvCreatePlace.text = entry.createPlace
        holder.tvCreateDate.text = entry.createDate
        if (entry.latitude != null && entry.longitude != null){
            holder.tvCoordinates.text = "${entry.latitude}, ${entry.longitude}"
        }

        holder.btnDelete.setOnClickListener {
            deleteEntry(holder.adapterPosition)
        }
        if (entry.isPersonal) {
            holder.imPersonal.setImageResource(R.drawable.baseline_person_24)
        } else {
            holder.imPersonal.setImageResource(R.drawable.baseline_build_24)
        }

    }

    fun deleteAll(){
        Thread {
            AppDatabase.getInstance(context).diaryDao().deleteAll()

            (context as ScrollingActivity).runOnUiThread {
                entries.clear()
                notifyDataSetChanged()
            }
        }.start()
    }

    fun deleteEntry(position: Int) {
        Thread {
            AppDatabase.getInstance(context).diaryDao().deleteEntry(entries[position])

            (context as ScrollingActivity).runOnUiThread {
                entries.removeAt(position)
                notifyItemRemoved(position)
            }
        }.start()
    }

    fun addEntry(entry: DiaryEntry) {
        entries.add(entry)
        //notifyDataSetChanged()
        notifyItemInserted(entries.lastIndex)
    }

    override fun onItemMoved(fromPosition: Int, toPosition: Int) {
        Collections.swap(entries, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onDismissed(position: Int) {
        deleteEntry(position)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvEntryTitle = itemView.tvEntryTitle
        val tvEntryText = itemView.tvEntryText
        val tvCreatePlace = itemView.tvCreatePlace
        val tvCreateDate = itemView.tvCreateDate
        val btnDelete = itemView.btnDelete
        val imPersonal = itemView.imPersonal
        val tvCoordinates = itemView.tvCoordinates
    }
}