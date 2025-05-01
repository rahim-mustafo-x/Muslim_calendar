package uz.coder.muslimcalendar.models.internet

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Weekday(
    @SerialName("en")
    val en: String,
    @SerialName("ar")
    val ar: String? = null
)