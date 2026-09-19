package uz.coder.muslimcalendar.domain.model

data class SuraAyah(
    var id: String = "",
    var sura: String = "",
    var aya: String = "",
    var arabicText: String = "",
    var translation: String = "",
    var footnotes: String = "")