package uz.coder.muslimcalendar.ktor.internet

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Hijri(
    @SerialName("date")
    val date: String,
    @SerialName("format")
    val format: String,
    @SerialName("day")
    val day: String,
    @SerialName("weekday")
    val weekday: Weekday,
    @SerialName("month")
    val month: Month,
    @SerialName("year")
    val year: String,
    @SerialName("designation")
    val designation: Designation,
    @SerialName("holidays")
    val holidays: List<String>,
    @SerialName("adjustedHolidays")
    val adjustedHolidays: List<String>,
    @SerialName("method")
    val method: String
)