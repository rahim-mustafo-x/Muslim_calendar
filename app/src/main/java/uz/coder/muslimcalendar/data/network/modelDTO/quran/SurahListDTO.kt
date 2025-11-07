package uz.coder.muslimcalendar.data.network.modelDTO.quran

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class SurahListDTO(
    @SerializedName("arabic_text")
    val arabicText: String? = null,
    @SerializedName("aya")
    val aya: String? = null,
    @SerializedName("footnotes")
    val footnotes: String? = null,
    @SerializedName("id")
    val id: String? = null,
    @SerializedName("sura")
    val sura: String? = null,
    @SerializedName("translation")
    val translation: String? = null
)