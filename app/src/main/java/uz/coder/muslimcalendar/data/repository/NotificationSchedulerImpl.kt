package uz.coder.muslimcalendar.data.repository

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import uz.coder.muslimcalendar.R
import uz.coder.muslimcalendar.SharedPref
import uz.coder.muslimcalendar.data.db.AppDatabase
import uz.coder.muslimcalendar.data.map.CalendarMap
import uz.coder.muslimcalendar.data.receiver.AlarmBroadCast
import uz.coder.muslimcalendar.data.service.QazoReminderWorker
import uz.coder.muslimcalendar.domain.model.MuslimCalendar
import uz.coder.muslimcalendar.domain.repository.NotificationScheduler
import uz.coder.muslimcalendar.todo.KEY_ASR
import uz.coder.muslimcalendar.todo.KEY_BOMDOD
import uz.coder.muslimcalendar.todo.KEY_PESHIN
import uz.coder.muslimcalendar.todo.KEY_QUYOSH
import uz.coder.muslimcalendar.todo.KEY_SHOM
import uz.coder.muslimcalendar.todo.KEY_XUFTON
import java.time.chrono.HijrahDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationSchedulerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val db: AppDatabase,
    private val sharedPref: SharedPref,
    private val map: CalendarMap
) : NotificationScheduler {

    private val iconFlows = listOf(
        MutableStateFlow(sharedPref.getInt(KEY_BOMDOD, R.drawable.ic_speaker_on)),
        MutableStateFlow(sharedPref.getInt(KEY_QUYOSH, R.drawable.ic_bell)),
        MutableStateFlow(sharedPref.getInt(KEY_PESHIN, R.drawable.ic_speaker_on)),
        MutableStateFlow(sharedPref.getInt(KEY_ASR, R.drawable.ic_speaker_on)),
        MutableStateFlow(sharedPref.getInt(KEY_SHOM, R.drawable.ic_speaker_on)),
        MutableStateFlow(sharedPref.getInt(KEY_XUFTON, R.drawable.ic_speaker_on))
    )

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
                val now = Calendar.getInstance()
                val (h, m) = todayData.hufton.split(":").map { it.toInt() }
                val xuftonTime = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, h)
                    set(Calendar.MINUTE, m)
                }
                
                if (now.after(xuftonTime) && dayList.size > 1) {
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
        val enabled = sharedPref.getBoolean("follow_up_enabled", true)
        if (!enabled) return

        val delayMinutes = sharedPref.getInt("follow_up_delay", 15)
        
        val workRequest = OneTimeWorkRequestBuilder<QazoReminderWorker>()
            .setInitialDelay(delayMinutes.toLong(), TimeUnit.MINUTES)
            .setInputData(workDataOf("is_daily_follow_up" to true))
            .build()
        WorkManager.getInstance(context).enqueue(workRequest)
    }

    private fun cancelAllAlarms(alarmManager: AlarmManager) {
        for (month in 0..11)
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
            Triple(item.tongSaharlik, iconFlows[0].value, 0),
            Triple(item.sunRise, iconFlows[1].value, 1),
            Triple(item.peshin, iconFlows[2].value, 2),
            Triple(item.asr, iconFlows[3].value, 3),
            Triple(item.shomIftor, iconFlows[4].value, 4),
            Triple(item.hufton, iconFlows[5].value, 5)
        )

        prayerTimes.forEach { (time, iconRes, index) ->
            if (iconRes == R.drawable.ic_speaker_cross || iconRes == -1) return@forEach
            val (hour, minute) = time.split(":").map { it.toIntOrNull() ?: 0 }
            val calendar = Calendar.getInstance().apply {
                set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR))
                set(Calendar.MONTH, item.month)
                set(Calendar.DAY_OF_MONTH, item.day)
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute + adjustments[index])
                set(Calendar.SECOND, 0)
                if (timeInMillis <= System.currentTimeMillis()) return@forEach // Don't schedule past events
            }

            val music = when(iconRes) {
                R.drawable.ic_speaker_on -> R.raw.azan
                R.drawable.ic_bell -> -2
                else -> -1
            }

            val requestId = generateRequestId(item.month, item.day, index)
            val intent = AlarmBroadCast.getIntent(context, hour, minute, getPrayerName(index), music)
            val pendingIntent = PendingIntent.getBroadcast(
                context, requestId, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        }
    }

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