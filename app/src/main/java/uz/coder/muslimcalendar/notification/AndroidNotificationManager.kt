package uz.coder.muslimcalendar.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import uz.coder.muslimcalendar.data.receiver.AlarmBroadCast
import uz.coder.muslimcalendar.domain.model.MuslimCalendar
import uz.coder.muslimcalendar.domain.notification.NotificationManager
import java.util.*

class AndroidNotificationManager(private val context: Context) : NotificationManager {

    override fun schedulePrayerNotifications(calendar: MuslimCalendar) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        
        val prayerTimes = listOf(
            calendar.tongSaharlik to "Bomdod",
            calendar.sunRise to "Quyosh",
            calendar.peshin to "Peshin",
            calendar.asr to "Asr",
            calendar.shomIftor to "Shom",
            calendar.hufton to "Xufton"
        )

        prayerTimes.forEachIndexed { index, (timeStr, name) ->
            if (timeStr.isEmpty()) return@forEachIndexed
            val parts = timeStr.split(":")
            if (parts.size < 2) return@forEachIndexed
            val h = parts[0].toIntOrNull() ?: return@forEachIndexed
            val m = parts[1].toIntOrNull() ?: return@forEachIndexed
            
            val calendarInstance = Calendar.getInstance().apply {
                set(Calendar.YEAR, calendar.year)
                set(Calendar.MONTH, calendar.month - 1)
                set(Calendar.DAY_OF_MONTH, calendar.day)
                set(Calendar.HOUR_OF_DAY, h)
                set(Calendar.MINUTE, m)
                set(Calendar.SECOND, 0)
            }

            if (calendarInstance.timeInMillis > System.currentTimeMillis()) {
                val intent = AlarmBroadCast.getIntent(context, h, m, "$name namozi", -1)
                val pendingIntent = PendingIntent.getBroadcast(
                    context, 
                    generateRequestId(calendar.month, calendar.day, index), 
                    intent, 
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendarInstance.timeInMillis,
                    pendingIntent
                )
            }
        }
    }

    private fun generateRequestId(month: Int, day: Int, prayerIndex: Int): Int =
        month * 10000 + day * 100 + prayerIndex

    override fun cancelAllNotifications() {
        // Implement cancellation logic if needed
    }

    override fun areNotificationsEnabled(): Boolean {
        // Check Android permission
        return true
    }
}
