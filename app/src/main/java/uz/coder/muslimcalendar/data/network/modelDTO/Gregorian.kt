package uz.coder.muslimcalendar.data.network.modelDTO

import kotlinx.serialization.Serializable

@Serializable
data class Gregorian(
    val date: String? = null,
    val weekday: Weekday? = null,
)