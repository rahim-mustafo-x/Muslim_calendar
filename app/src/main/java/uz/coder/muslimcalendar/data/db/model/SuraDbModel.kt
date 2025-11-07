package uz.coder.muslimcalendar.data.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sura")
data class SuraDbModel(
    @PrimaryKey
    val number: Int,
    val name: String,
    val englishName: String,
    val englishNameTranslation: String,
    val numberOfAyahs: Int,
    val revelationType: String
)