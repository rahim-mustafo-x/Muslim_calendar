@file:Suppress("DEPRECATION")

package uz.coder.muslimcalendar.todo

import android.app.Activity
import android.app.Activity.ALARM_SERVICE
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.ConnectivityManager
import uz.coder.muslimcalendar.MainActivity
import uz.coder.muslimcalendar.models.model.Item
import java.util.Calendar

fun Context.isConnected():Boolean{
    val info =
        this.getConnectivityManager().activeNetworkInfo
    return info != null && info.isConnectedOrConnecting
}
fun Context.getConnectivityManager() = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

fun workReceiver(item: Item){
    val alarm = App.getApplication().getSystemService(ALARM_SERVICE) as AlarmManager
    val pendingIntent = PendingIntent.getBroadcast(App.getApplication(), 111, intent(App.getApplication(), item.time, item.name), PendingIntent.FLAG_IMMUTABLE)
    val hour = item.time.subSequence(0, 2).toString().toInt()
    val minute = item.time.subSequence(3, item.time.length).toString().toInt()
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, hour)
    calendar.set(Calendar.MINUTE, minute)
    calendar.set(Calendar.SECOND, 0)
    alarm.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
}
fun intent(context: Context, time: String, name: String) = Intent(context, MainActivity::class.java).apply { putExtra(ITEM_TIME, time); putExtra(
    ITEM_NAME, name) }

fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
