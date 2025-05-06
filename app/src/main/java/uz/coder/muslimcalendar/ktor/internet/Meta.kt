package uz.coder.muslimcalendar.ktor.internet

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Meta(
    @SerialName("latitude")
    val latitude: Double,
    @SerialName("longitude")
    val longitude: Double,
    @SerialName("timezone")
    val timezone: String,
    @SerialName("method")
    val method: Method,
    @SerialName("latitudeAdjustmentMethod")
    val latitudeAdjustmentMethod: String,
    @SerialName("midnightMode")
    val midnightMode: String,
    @SerialName("school")
    val school: String,
    @SerialName("offset")
    val offset: Map<String, Int>
)