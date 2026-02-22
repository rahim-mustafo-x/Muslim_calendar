package uz.coder.muslimcalendar.data.network.modelDTO.quran

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResultDTO<T>(
    @SerialName("code")
    val code: Int? = null,
    @SerialName("status")
    val status: String? = null,
    @SerialName("data")
    val data: T? = null
)
