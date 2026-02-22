package uz.coder.muslimcalendar.data.network.modelDTO

import kotlinx.serialization.Serializable

@Serializable
data class PrayerResponse(
    val code: Int? = null,
    val status: String? = null,
    val data: List<PrayerData>? = null
)