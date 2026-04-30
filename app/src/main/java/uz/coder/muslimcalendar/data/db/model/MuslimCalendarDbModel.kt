package uz.coder.muslimcalendar.data.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "muslimCalendar", primaryKeys = ["day", "month", "year"])
data class MuslimCalendarDbModel(
    val day: Int = 0,
    val month: Int = 0,
    val year: Int = 0,
    val weekday: String = "",
    val asr: String = "",
    val hufton: String = "",
    val peshin: String = "",
    val shomIftor: String = "",
    val tongSaharlik: String = "",
    val sunrise:String = ""
)