@file:Suppress("DEPRECATION")

package uz.coder.muslimcalendar.todo

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.net.ConnectivityManager
import uz.coder.muslimcalendar.db.model.SuraDbModel
import uz.coder.muslimcalendar.models.model.Item
import uz.coder.muslimcalendar.models.model.SuraAyah
import uz.coder.muslimcalendar.models.model.quran.SurahList
import uz.coder.muslimcalendar.repository.TranslateToUzbek
import kotlin.concurrent.thread

fun Context.isConnected():Boolean{
    val info =
        this.getConnectivityManager().activeNetworkInfo
    return info != null && info.isConnectedOrConnecting
}
fun Context.getConnectivityManager() = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
fun String.toArabicNumbers(): String {
    return this.map { char ->
        if (char in '0'..'9') {
            // Arab raqamlarining unicode boshlanishi: 0x0660
            ('\u0660' + (char - '0'))
        } else {
            char
        }
    }.joinToString("")
}

fun Long.formatTime(): String {
    val totalSeconds = this / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return if (hours > 0) {
        "%02d:%02d:%02d".format(hours, minutes, seconds)
    } else {
        "%02d:%02d".format(minutes, seconds)
    }
}
fun List<SuraAyah>.toAyahList(): List<SurahList> {
    return this.map { SurahList(arabicText = it.arabicText, aya = it.aya, footnotes =  it.footnotes, id =  it.id, sura =  it.sura, translation =  it.translation) }
}
fun List<SurahList>.toSuraAyah(): List<SuraAyah> {
    return this.map { SuraAyah(arabicText = it.arabicText, aya =  it.aya, footnotes =  it.footnotes, id =  it.id, sura =  it.sura, translation =  it.translation) }
}
fun Pair<List<String>, List<String>>.toItems(): List<Item> {
    val list = mutableListOf<Item>()
    list.clear()
    repeat(this.first.size){
        list.add(Item(this.first[it], this.second[it]))
    }
    return list
}
fun List<SuraDbModel>.translate(): List<SuraDbModel> {
    val list = mutableListOf<SuraDbModel>()
    thread(start = true) {
        this.forEach {
            val englishName = formatStrings(TranslateToUzbek.translateArabicToUzbek(it.englishName))?:""
            val englishNameTranslation = TranslateToUzbek.translateEnglishToUzbek(it.englishNameTranslation)
            val revelationType = when(it.revelationType){
                "Meccan"-> "Makka"
                "Medinan"-> "Madina"
                else -> {
                    "Makka"
                }
            }
            list.add(SuraDbModel(it.number,englishName,englishNameTranslation,it.name,revelationType))
        }
    }.join()
    return list
}

fun formatStrings(input: String?): String? {
    if (input.isNullOrEmpty()) return input

    var trimmed = input.trim().replace("\\s+".toRegex(), " ").replace("\\s*-\\s*".toRegex(), "-")

    if (trimmed.lowercase().startsWith("aal-i-")) {
        trimmed = "Al-" + trimmed.substring(6)
    }

    trimmed = trimmed.replace("aa", "a", ignoreCase = true)
        .replace('w', 'v', ignoreCase = false)
        .replace('W', 'V')

    val parts = trimmed.split("-")
    val result = parts.filter { it.isNotEmpty() }
        .joinToString("-") { part ->
            if (part.length == 1) part.uppercase()
            else part.substring(0, 1).uppercase() + part.substring(1).lowercase()
        }

    return result
}