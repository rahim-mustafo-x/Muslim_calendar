package uz.coder.muslimcalendar.data.ktor.internet

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import uz.coder.muslimcalendar.data.ktor.internet.Date

@Serializable
data class PrayerData(
    @SerialName("timings")
    val timings: Timings,
    @SerialName("date")
    val date: Date,
    @SerialName("meta")
    val meta: Meta
)