package uz.coder.muslimcalendar.data.network.modelDTO

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Timings(
    @SerialName("Fajr") val fajr: String,
    @SerialName("Sunrise") val sunrise: String,
    @SerialName("Dhuhr") val dhuhr: String,
    @SerialName("Asr") val asr: String,
    @SerialName("Sunset") val sunset: String,
    @SerialName("Maghrib") val maghrib: String,
    @SerialName("Isha") val isha: String,
)