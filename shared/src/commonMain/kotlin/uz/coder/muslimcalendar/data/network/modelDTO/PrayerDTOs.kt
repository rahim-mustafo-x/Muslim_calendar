package uz.coder.muslimcalendar.shared.data.network.modelDTO

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PrayerResponse(
    val code: Int? = null,
    val status: String? = null,
    val data: List<PrayerData>? = null
)

@Serializable
data class PrayerData(
    val timings: Timings? = null,
    val date: PrayerDate? = null,
)

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

@Serializable
data class PrayerDate(
    val gregorian: Gregorian? = null
)

@Serializable
data class Gregorian(
    val date: String? = null,
    val day: String? = null,
    val month: Month? = null,
    val year: String? = null,
    val weekday: Weekday? = null,
)

@Serializable
data class Month(
    val number: Int? = null,
    val en: String? = null
)

@Serializable
data class Weekday(
    val en: String? = null
)
