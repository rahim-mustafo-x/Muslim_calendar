package uz.coder.muslimcalendar.todo

val hijriMonthTranslations = mapOf(
    "Muharram" to mapOf(
        UZB to "Muharram",
        ENG to "Muharram",
        RUS to "Мухаррам"
    ),
    "Safar" to mapOf(
        UZB to "Safar",
        ENG to "Safar",
        RUS to "Сафар"
    ),
    "Rabi al-awwal" to mapOf(
        UZB to "Rabiul avval",
        ENG to "Rabi al-awwal",
        RUS to "Раби аль-авваль"
    ),
    "Rabi al-thani" to mapOf(
        UZB to "Rabius sani",
        ENG to "Rabi al-thani",
        RUS to "Раби ас-сани"
    ),
    "Jumada al-awwal" to mapOf(
        UZB to "Jumadil avval",
        ENG to "Jumada al-awwal",
        RUS to "Джумада аль-уля"
    ),
    "Jumada al-thani" to mapOf(
        UZB to "Jumadis sani",
        ENG to "Jumada al-thani",
        RUS to "Джумада ас-сания"
    ),
    "Rajab" to mapOf(
        UZB to "Rajab",
        ENG to "Rajab",
        RUS to "Раджаб"
    ),
    "Shaʿbān" to mapOf(
        UZB to "Sha’bon",
        ENG to "Shaʿbān",
        RUS to "Шаабан"
    ),
    "Ramadan" to mapOf(
        UZB to "Ramazon",
        ENG to "Ramadan",
        RUS to "Рамадан"
    ),
    "Shawwāl" to mapOf(
        UZB to "Shawvol",
        ENG to "Shawwāl",
        RUS to "Шавваль"
    ),
    "Dhū al-Qaʿdah" to mapOf(
        UZB to "Zulqa’da",
        ENG to "Dhū al-Qaʿdah",
        RUS to "Зуль-када"
    ),
    "Dhū al-Ḥijjah" to mapOf(
        UZB to "Zulhijja",
        ENG to "Dhū al-Ḥijjah",
        RUS to "Зуль-хиджа"
    )
)

fun String.toNormalTranslate(): String {
    val translations = hijriMonthTranslations[this]
    return translations?.get(appLanguage) ?: ""
}