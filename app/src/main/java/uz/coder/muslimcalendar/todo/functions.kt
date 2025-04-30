@file:Suppress("DEPRECATION")

package uz.coder.muslimcalendar.todo

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.net.ConnectivityManager

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
    val result = StringBuilder()

    // Har bir belgini tekshirib chiqamiz
    for (c in this.toCharArray()) {
        // Agar belgi raqam bo'lsa, uni arab raqamiga o'zgartiramiz
        if (Character.isDigit(c)) {
            // '0' belgisining unicode qiymatiga 0x0660 ni qo'shish orqali arab raqamlariga o'tamiz
            result.append((c.code + 0x0660 - '0'.code).toChar())
        } else {
            // Agar belgi raqam bo'lmasa, uni o'zgartirmay qo'yamiz
            result.append(c)
        }
    }

    return result.toString()
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