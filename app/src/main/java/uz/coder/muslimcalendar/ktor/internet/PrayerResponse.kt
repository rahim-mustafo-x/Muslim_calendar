package uz.coder.muslimcalendar.ktor.internet

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PrayerResponse<T>(
    @SerialName("code")
    val code: Int,
    @SerialName("status")
    val status: String,
    @SerialName("data")
    val data: T
)