package uz.coder.muslimcalendar.models.internet

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Month(
    @SerialName("number")
    val number: Int,
    @SerialName("en")
    val en: String,
    @SerialName("ar")
    val ar: String? = null,
    @SerialName("days")
    val days: Int? = null
)