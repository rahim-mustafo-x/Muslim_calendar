package uz.coder.muslimcalendar.domain.model.quran

import kotlinx.serialization.Serializable

@Serializable
data class SurahList(
    val id: String,
    val sura: String,
    val aya: String,
    val arabicText: String,
    val translation: String,
    val footnotes: String)