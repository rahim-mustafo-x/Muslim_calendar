package uz.coder.muslimcalendar.todo

val hijriMonthTranslations = mapOf(
    "Muharram"
        to "Muharram",
    "Safar"
        to "Safar",
    "Rabi al-awwal"
        to "Rabiul avval",
    "Rabi al-thani"
        to "Rabius sani",
    "Jumada al-awwal"
        to "Jumadil avval",
    "Jumada al-thani"
        to "Jumadis sani",
    "Rajab"
        to "Rajab",
    "Shaʿbān"
        to "Sha’bon",
    "Ramadan"
        to "Ramazon",
    "Shawwāl"
        to "Shawvol",
    "Dhū al-Qaʿdah"
        to "Zulqa’da",
    "Dhū al-Ḥijjah"
        to "Zulhijja"
)

fun String.toNormalTranslate(): String {
    return hijriMonthTranslations[this]?:""
}