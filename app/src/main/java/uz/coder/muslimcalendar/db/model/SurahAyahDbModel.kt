package uz.coder.muslimcalendar.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "surah_list")
data class SurahAyahDbModel(
    val arabicText: String = "",
    val aya: String = "",
    val footnotes: String = "",
    val sura: String = "",
    val translation: String = "",
    val surahAudioPath: String = "",
    @PrimaryKey
    val id: String = ""
)