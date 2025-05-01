package uz.coder.muslimcalendar.models.internet

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PrayerData(
    @SerialName("timings")
    val timings: Timings,
    @SerialName("date")
    val date: Date,
    @SerialName("meta")
    val meta: Meta
)