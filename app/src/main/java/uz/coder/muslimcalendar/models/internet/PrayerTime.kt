package uz.coder.muslimcalendar.models.internet


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PrayerTime(
    @SerialName("date")
    val date: String = "",
    @SerialName("day")
    val day: Int = 0,
    @SerialName("hijri_date")
    val hijriDate: HijriDate = HijriDate(),
    @SerialName("month")
    val month: Int = 0,
    @SerialName("region")
    val region: String = "",
    @SerialName("regionNumber")
    val regionNumber: Int = 0,
    @SerialName("times")
    val times: Times = Times(),
    @SerialName("weekday")
    val weekday: String = ""
)