package uz.coder.muslimcalendar.data.ktor.internet.quran


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SuraDTO(
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