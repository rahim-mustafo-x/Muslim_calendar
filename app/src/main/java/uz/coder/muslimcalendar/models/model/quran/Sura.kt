package uz.coder.muslimcalendar.models.model.quran

data class Sura(
    val ayahs: List<Ayah>,
    val englishName: String,
    val englishNameTranslation: String,
    val name: String,
    val number: Int,
    val revelationType: String
)