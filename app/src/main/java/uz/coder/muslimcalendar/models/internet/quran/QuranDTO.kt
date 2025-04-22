package uz.coder.muslimcalendar.models.internet.quran

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QuranDTO(
    @SerialName("surahs")
    val surahs: List<SuraDTO>? = null
)