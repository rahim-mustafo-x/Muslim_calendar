package uz.coder.muslimcalendar.data.network.modelDTO

import kotlinx.serialization.Serializable

@Serializable
data class PrayerData(
    val timings: Timings? = null,
    val date: Date? = null,
)