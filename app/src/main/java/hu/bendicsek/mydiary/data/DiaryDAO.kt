package hu.bendicsek.mydiary.data

import androidx.room.*

@Dao
interface DiaryDAO {

    @Query("SELECT * FROM diary")
    fun getAllEntries() : List<DiaryEntry>

    @Insert
    fun addEntry(entry: DiaryEntry): Long

    @Delete
    fun deleteEntry(entry: DiaryEntry)

    @Update
    fun updateEntry(entry: DiaryEntry)

    @Query("DELETE FROM diary")
    fun deleteAll()

}