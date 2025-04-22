package uz.coder.muslimcalendar.models.model.quran

data class Ayah(
    val hizbQuarter: Int,
    val juz: Int,
    val manzil: Int,
    val number: Int,
    val numberInSurah: Int,
    val page: Int,
    val ruku: Int,
    val text: String
)