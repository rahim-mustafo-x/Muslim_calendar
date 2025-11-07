package uz.coder.muslimcalendar.data.network.modelDTO.quran

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class SurahDTO(
    @SerializedName("result")
    val result: List<SurahListDTO?>?=null,
)
