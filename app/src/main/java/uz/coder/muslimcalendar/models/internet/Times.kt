package uz.coder.muslimcalendar.models.internet


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Times(
    @SerialName("asr")
    val asr: String = "",
    @SerialName("hufton")
    val hufton: String = "",
    @SerialName("peshin")
    val peshin: String = "",
    @SerialName("quyosh")
    val quyosh: String = "",
    @SerialName("shom_iftor")
    val shomIftor: String = "",
    @SerialName("tong_saharlik")
    val tongSaharlik: String = ""
)