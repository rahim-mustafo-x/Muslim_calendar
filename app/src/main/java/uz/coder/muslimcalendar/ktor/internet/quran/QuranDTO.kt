package uz.coder.muslimcalendar.ktor.internet.quran

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QuranDTO(
    @SerialName("surahs")
    val surahs: List<SuraDTO>? = null
)