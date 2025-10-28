package uz.coder.muslimcalendar.data.ktor.internet

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Date(
    @SerialName("readable")
    val readable: String,
    @SerialName("timestamp")
    val timestamp: String,
    @SerialName("gregorian")
    val gregorian: Gregorian,
    @SerialName("hijri")
    val hijri: Hijri
)