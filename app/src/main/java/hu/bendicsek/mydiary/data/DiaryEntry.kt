package hu.bendicsek.mydiary.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "diary")
data class DiaryEntry(
    @PrimaryKey(autoGenerate = true) var diaryEntryId: Long?,
    @ColumnInfo(name = "createdate") var createDate: String,
    @ColumnInfo(name = "diaryentrytitle") var diaryEntryTitle: String,
    @ColumnInfo(name = "diaryentrytext") var diaryEntryText: String,
    @ColumnInfo(name = "ispersonal") var isPersonal: Boolean,
    @ColumnInfo(name = "createplace") var createPlace: String,
    @ColumnInfo(name = "longitude") var longitude: Double?,
    @ColumnInfo(name = "latitude") var latitude: Double?
) : Serializable
