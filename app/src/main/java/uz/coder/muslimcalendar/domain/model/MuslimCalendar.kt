package uz.coder.muslimcalendar.domain.model

data class MuslimCalendar(
    val day: Int = 0,
    val month: Int = 0,
    val weekday: String = "",
    val asr: String = "",
    val hufton: String = "",
    val peshin: String = "",
    val shomIftor: String = "",
    val tongSaharlik: String = "",
    val sunRise: String = ""
) {
    val item = listOf(
        tongSaharlik,
        sunRise,
        peshin,
        asr,
        shomIftor,
        hufton
    )
}
