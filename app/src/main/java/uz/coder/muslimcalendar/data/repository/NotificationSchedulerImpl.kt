package uz.coder.muslimcalendar.data.repository

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.flow.first
import uz.coder.muslimcalendar.R
import uz.coder.muslimcalendar.SharedPref
import uz.coder.muslimcalendar.data.db.AppDatabase
import uz.coder.muslimcalendar.data.map.CalendarMap
import uz.coder.muslimcalendar.data.receiver.AlarmBroadCast
import uz.coder.muslimcalendar.shared.domain.model.MuslimCalendar
import uz.coder.muslimcalendar.domain.repository.NotificationScheduler
import uz.coder.muslimcalendar.todo.KEY_ASR
import uz.coder.muslimcalendar.todo.KEY_BOMDOD
import uz.coder.muslimcalendar.todo.KEY_PESHIN
import uz.coder.muslimcalendar.todo.KEY_SHOM
import uz.coder.muslimcalendar.todo.KEY_XUFTON
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.Calendar

class NotificationSchedulerImpl(
    private val context: Context,
    private val db: AppDatabase,
    private val sharedPref: SharedPref,
    private val map: CalendarMap
) : NotificationScheduler {

    @SuppressLint("ScheduleExactAlarm")
    override suspend fun scheduleAllAlarms() {
        try {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val today = Calendar.getInstance()
            val currentDay = today.get(Calendar.DAY_OF_MONTH)
            val currentMonth = today.get(Calendar.MONTH) + 1
            val currentYear = today.get(Calendar.YEAR)
            
            // Get today's data to check if we should schedule for today or tomorrow
            val dayList = db.calendarDao().fromTodayOnwards(currentDay, currentMonth, currentYear).first()
            if (dayList.isNotEmpty()) {
                val todayData = map.toMuslimCalendar(dayList[0])
                scheduleDay(todayData, alarmManager)
                
                // If it's after Xufton, schedule for tomorrow as well
                val xuftonTime = todayData.hufton.toLocalTimeOrNull()
                val isAfterXufton = xuftonTime != null && !LocalTime.now().isBefore(xuftonTime)

                // Before Xufton, only the remaining prayers of today are scheduled.
                // Once Xufton begins, tomorrow's five prayers are also scheduled.
                if (isAfterXufton && dayList.size > 1) {
                    scheduleDay(map.toMuslimCalendar(dayList[1]), alarmManager)
                }
            }
            
            // Schedule a refresh at 00:00
            scheduleMidnightRefresh(alarmManager)
            
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun scheduleMidnightRefresh(alarmManager: AlarmManager) {
        val midnight = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }
        
        val intent = Intent(context, AlarmBroadCast::class.java).apply {
            action = "ACTION_REFRESH_ALARMS"
        }
        val pi = PendingIntent.getBroadcast(
            context, 9999, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, midnight.timeInMillis, pi)
    }

    override suspend fun rescheduleAll() {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        cancelAllAlarms(alarmManager)
        scheduleAllAlarms()
        scheduleDailyNotification()
    }

    override suspend fun scheduleDailyNotification() {
        val enabled = sharedPref.getBoolean("daily_notif_enabled", true)
        if (!enabled) return

        val time = sharedPref.getString("daily_notif_time", "00:00")
        val (hour, minute) = time.split(":").map { it.toIntOrNull() ?: 0 }
        
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }

        val intent = Intent(context, AlarmBroadCast::class.java).apply {
            action = "ACTION_DAILY_NOTIFICATION"
        }
        val pi = PendingIntent.getBroadcast(
            context, 8888, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pi)
    }

    override suspend fun scheduleFollowUpReminder() {
        // Follow-ups are scheduled per individual prayer by AlarmBroadCast (+30 minutes).
    }

    private fun cancelAllAlarms(alarmManager: AlarmManager) {
        for (month in 1..12)
            for (day in 1..31)
                for (i in 0..5) {
                    val id = generateRequestId(month, day, i)
                    val intent = Intent(context, AlarmBroadCast::class.java)
                    val pi = PendingIntent.getBroadcast(
                        context, id, intent,
                        PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
                    )
                    pi?.let {
                        alarmManager.cancel(it)
                        it.cancel()
                    }
                }
    }

    private fun scheduleDay(item: MuslimCalendar, alarmManager: AlarmManager) {
        val adjustments = listOf<Int>(
            sharedPref.getInt("adj_bomdod", 0),
            sharedPref.getInt("adj_quyosh", 0),
            sharedPref.getInt("adj_peshin", 0),
            sharedPref.getInt("adj_asr", 0),
            sharedPref.getInt("adj_shom", 0),
            sharedPref.getInt("adj_xufton", 0)
        )
        
        val prayerTimes = listOf(
            Triple(item.tongSaharlik, "bomdod", 0),
            Triple(item.peshin, "peshin", 2),
            Triple(item.asr, "asr", 3),
            Triple(item.shomIftor, "shom", 4),
            Triple(item.hufton, "xufton", 5)
        )

        prayerTimes.forEach { (time, prayerKey, index) ->
            val legacyIconKey = when (prayerKey) {
                "bomdod" -> KEY_BOMDOD
                "peshin" -> KEY_PESHIN
                "asr" -> KEY_ASR
                "shom" -> KEY_SHOM
                else -> KEY_XUFTON
            }
            val notificationMode = sharedPref.getInt(legacyIconKey, R.drawable.ic_speaker_on)
            if (notificationMode == R.drawable.ic_speaker_cross) return@forEach
            val soundRes = if (notificationMode == R.drawable.ic_bell) -1
                else sharedPref.getInt("azan_sound_$prayerKey", R.raw.azan)
            val prayerTime = time.toLocalTimeOrNull() ?: return@forEach
            val triggerAt = runCatching {
                LocalDateTime.of(
                    LocalDate.of(item.year, item.month, item.day),
                    prayerTime
                ).plusMinutes(adjustments[index].toLong())
            }.getOrNull() ?: return@forEach
            if (!triggerAt.isAfter(LocalDateTime.now())) return@forEach

            val music = if (soundRes > 0) soundRes else -1

            val requestId = generateRequestId(item.month, item.day, index)
            val prayerName = getPrayerName(index)
            val eventId = "${item.year}${item.month.toString().padStart(2, '0')}${item.day.toString().padStart(2, '0')}_${index}"
            val intent = AlarmBroadCast.getIntent(
                context, triggerAt.hour, triggerAt.minute, prayerName, music, eventId
            )
            val pendingIntent = PendingIntent.getBroadcast(
                context, requestId, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val triggerAtMillis = triggerAt.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
        }
    }

    private fun String.toLocalTimeOrNull(): LocalTime? = runCatching {
        LocalTime.parse(trim().take(5))
    }.getOrNull()

    private fun generateRequestId(month: Int, day: Int, prayerIndex: Int): Int =
        month * 10000 + day * 100 + prayerIndex

    private fun getPrayerName(index: Int): String = when(index) {
        0 -> "Bomdod namozi"
        1 -> "Quyosh chiqishi"
        2 -> "Peshin namozi"
        3 -> "Asr namozi"
        4 -> "Shom namozi"
        5 -> "Xufton namozi"
        else -> "Namoz vaqti"
    }
}
