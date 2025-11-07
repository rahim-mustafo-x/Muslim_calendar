package uz.coder.muslimcalendar.data.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "surahAyah")
data class SurahAyahDbModel(
    val arabicText: String = "",
    val aya: String = "",
    val footnotes: String = "",
    val sura: String = "",
    val translation: String = "",
    @PrimaryKey
    val id: String = ""
)