package uz.coder.muslimcalendar.data.db.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "audioPath",
    indices = [Index(value = ["sura"], unique = true)]
)
data class AudioPathDbModel(
    val sura: String,
    val audioPath: String
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
