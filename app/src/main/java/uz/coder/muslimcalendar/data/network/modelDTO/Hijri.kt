package uz.coder.muslimcalendar.data.network.modelDTO

import kotlinx.serialization.Serializable

@Serializable
data class Hijri(
    val day: String? = null,
    val weekday: Weekday? = null,
    val month: Month? = null
)