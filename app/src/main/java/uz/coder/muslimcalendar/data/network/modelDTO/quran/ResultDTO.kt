package uz.coder.muslimcalendar.data.network.modelDTO.quran

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class  ResultDTO <T>(
    @SerializedName("code")
    val code:Int?=null,
    @SerializedName("status")
    val status:String?=null,
    @SerializedName("data")
    val data:T? = null
)
