package uz.coder.muslimcalendar.models.model.quran

data class SurahList(
    val id: String,
    val sura: String,
    val aya: String,
    val arabicText: String,
    val translation: String,
    val footnotes: String,
    val audioPath:String)