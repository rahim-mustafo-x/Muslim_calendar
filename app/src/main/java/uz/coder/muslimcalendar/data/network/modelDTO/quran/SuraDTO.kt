package uz.coder.muslimcalendar.data.network.modelDTO.quran

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SuraDTO(
    @SerialName("number")
    val number: Int? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("englishName")
    val englishName: String? = null,
    @SerialName("englishNameTranslation")
    val englishNameTranslation: String? = null,
    @SerialName("numberOfAyahs")
    val numberOfAyahs: Int? = null,
    @SerialName("revelationType")
    val revelationType: String? = null
)