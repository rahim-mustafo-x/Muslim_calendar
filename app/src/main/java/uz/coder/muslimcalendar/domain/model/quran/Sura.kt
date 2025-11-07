package uz.coder.muslimcalendar.domain.model.quran

data class Sura(
    val englishName: String,
    val englishNameTranslation: String,
    val name: String,
    val number: Int,
    val revelationType: String,
    val numberOfAyahs:Int
)