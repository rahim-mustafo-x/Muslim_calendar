package uz.coder.muslimcalendar.models.internet.quran


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SuraDTO(
    @SerialName("ayahs")
    val ayahDTOS: List<AyahDTO>? = null,
    @SerialName("englishName")
    val englishName: String? = null,
    @SerialName("englishNameTranslation")
    val englishNameTranslation: String? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("number")
    val number: Int? = null,
    @SerialName("revelationType")
    val revelationType: String? = null
)