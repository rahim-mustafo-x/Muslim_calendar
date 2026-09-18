package uz.coder.muslimcalendar.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class MuslimCalendar(
    val day: Int = 0,
    val month: Int = 0,
    val year: Int = 0,
    val weekday: String = "",
    val asr: String = "",
    val hufton: String = "",
    val peshin: String = "",
    val shomIftor: String = "",
    val tongSaharlik: String = "",
    val sunRise: String = ""
) {
    val items = listOf(
        tongSaharlik,
        sunRise,
        peshin,
        asr,
        shomIftor,
        hufton
    )
    val item = items
}
