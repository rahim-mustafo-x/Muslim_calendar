package uz.coder.muslimcalendar.models.internet.quran


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AyahDTO(
    @SerialName("hizbQuarter")
    val hizbQuarter: Int? = null,
    @SerialName("juz")
    val juz: Int? = null,
    @SerialName("manzil")
    val manzil: Int? = null,
    @SerialName("number")
    val number: Int? = null,
    @SerialName("numberInSurah")
    val numberInSurah: Int? = null,
    @SerialName("page")
    val page: Int? = null,
    @SerialName("ruku")
    val ruku: Int? = null,
    @SerialName("text")
    val text: String? = null
)