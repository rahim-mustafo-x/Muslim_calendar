package uz.coder.muslimcalendar.models.internet

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Method(
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String,
    @SerialName("params")
    val params: Map<String, Int>,
    @SerialName("location")
    val location: Map<String, Double>
)