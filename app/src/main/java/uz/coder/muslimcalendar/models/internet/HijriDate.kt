package uz.coder.muslimcalendar.models.internet


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HijriDate(
    @SerialName("day")
    val day: Int = 0,
    @SerialName("month")
    val month: String = ""
)