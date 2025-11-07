package uz.coder.muslimcalendar.data.network.modelDTO.quran

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class SuraDTO(
    @SerializedName("number")
    val number: Int? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("englishName")
    val englishName: String? = null,
    @SerializedName("englishNameTranslation")
    val englishNameTranslation: String? = null,
    @SerializedName("numberOfAyahs")
    val numberOfAyahs: Int? = null,
    @SerializedName("revelationType")
    val revelationType: String? = null
)