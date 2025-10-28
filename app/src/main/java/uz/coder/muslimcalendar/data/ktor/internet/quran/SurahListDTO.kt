package uz.coder.muslimcalendar.data.ktor.internet.quran


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SurahListDTO(
    @SerialName("arabic_text")
    val arabicText: String? = null,
    @SerialName("aya")
    val aya: String? = null,
    @SerialName("footnotes")
    val footnotes: String? = null,
    @SerialName("id")
    val id: String? = null,
    @SerialName("sura")
    val sura: String? = null,
    @SerialName("translation")
    val translation: String? = null
)