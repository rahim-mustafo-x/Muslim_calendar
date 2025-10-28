package uz.coder.muslimcalendar.data.ktor.internet

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Gregorian(
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
    @SerialName("lunarSighting")
    val lunarSighting: Boolean
)