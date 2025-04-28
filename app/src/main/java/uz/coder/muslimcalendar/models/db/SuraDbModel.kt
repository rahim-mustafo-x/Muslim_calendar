package uz.coder.muslimcalendar.models.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sura")
data class SuraDbModel(
    @PrimaryKey
    val number: Int,
    val englishName: String,
    val englishNameTranslation: String,
    val name: String,
    val revelationType: String
)