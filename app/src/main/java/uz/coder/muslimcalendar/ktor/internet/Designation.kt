package uz.coder.muslimcalendar.ktor.internet

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Designation(
    @SerialName("abbreviated")
    val abbreviated: String,
    @SerialName("expanded")
    val expanded: String
)