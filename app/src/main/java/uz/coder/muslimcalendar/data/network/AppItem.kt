package uz.coder.muslimcalendar.data.network

import kotlinx.serialization.Serializable

@Serializable
data class AppItem(
    val name: String,
    val iconUrl: String,
    val directUrl: String,
    val description: String? = null
)
