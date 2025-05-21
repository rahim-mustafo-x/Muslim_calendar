@file:Suppress("DEPRECATION")

package uz.coder.muslimcalendar.todo

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.net.ConnectivityManager
import uz.coder.muslimcalendar.models.model.Item
import uz.coder.muslimcalendar.models.model.SuraAyah
import uz.coder.muslimcalendar.models.model.quran.SurahList

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
    return listOf(
        Item(
            this.first[0],
            this.second[0]
        ),
        Item(
            this.first[1],
            this.second[1]
        ),
        Item(
            this.first[2],
            this.second[2]
        ),
        Item(
            this.first[3],
            this.second[3]
        ),
        Item(
            this.first[4],
            this.second[4]
        ),
        Item(
            this.first[5],
            this.second[5]
        ),
        Item(
            this.first[6],
            this.second[6]
        )
    )
}