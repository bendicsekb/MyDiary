package hu.bendicsek.mydiary.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = arrayOf(DiaryEntry::class), version = 2)
abstract class AppDatabase : RoomDatabase() {

    abstract fun diaryDao(): DiaryDAO

    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase::class.java, "diary.db")
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return INSTANCE!!
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}