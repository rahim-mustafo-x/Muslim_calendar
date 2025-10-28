package uz.coder.muslimcalendar.data.ktor.internet.quran

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import uz.coder.muslimcalendar.ktor.internet.quran.SuraDTO

@Serializable
data class QuranDTO(
    @SerialName("surahs")
    val surahs: List<SuraDTO>? = null
)