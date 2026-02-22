package uz.coder.muslimcalendar.data.network.modelDTO

import kotlinx.serialization.Serializable

@Serializable
data class Date(
    val gregorian: Gregorian? = null
)