package uz.coder.muslimcalendar.data.network.modelDTO

import kotlinx.serialization.SerialName

data class Timings(
    @SerialName("Fajr")
    val fajr: String? = null,
    @SerialName("Sunrise")
    val sunrise: String? = null,
    @SerialName("Dhuhr")
    val dhuhr: String? = null,
    @SerialName("Asr")
    val asr: String? = null,
    @SerialName("Maghrib")
    val maghrib: String? = null,
    @SerialName("Isha")
    val isha: String? = null,
)