package uz.coder.muslimcalendar.models.internet.quran

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class  ResultDTO <T>(
    @SerialName("code")
    val code:Int? = 0,
    @SerialName("status")
    val status:String? = "",
    @SerialName("data")
    val data:T? = null
)
