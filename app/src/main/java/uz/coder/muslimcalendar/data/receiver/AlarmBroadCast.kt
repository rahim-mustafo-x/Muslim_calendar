package uz.coder.muslimcalendar.data.receiver

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import androidx.core.app.NotificationCompat
import dagger.hilt.android.AndroidEntryPoint
import uz.coder.muslimcalendar.R

@AndroidEntryPoint
class AlarmBroadCast : BroadcastReceiver() {

    @javax.inject.Inject
    lateinit var sharedPref: uz.coder.muslimcalendar.SharedPref

    companion object {
        var mediaPlayer: MediaPlayer? = null

        private const val EXTRA_TEXT = "extra_text"
        private const val EXTRA_HOUR = "extra_hour"
        private const val EXTRA_MINUTE = "extra_minute"
        private const val EXTRA_MUSIC = "extra_music"

        fun getIntent(context: Context, hour: Int, minute: Int, text: String, musicResId: Int): Intent {
            return Intent(context, AlarmBroadCast::class.java).apply {
                putExtra(EXTRA_TEXT, text)
                putExtra(EXTRA_HOUR, hour)
                putExtra(EXTRA_MINUTE, minute)
                putExtra(EXTRA_MUSIC, musicResId)
            }
        }
    }

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return
        
        when (intent.action) {
            "ACTION_REFRESH_ALARMS" -> {
                val workRequest = androidx.work.OneTimeWorkRequestBuilder<uz.coder.muslimcalendar.data.service.PrayerAlarmWorker>().build()
                androidx.work.WorkManager.getInstance(context).enqueue(workRequest)
                return
            }
            "ACTION_DAILY_NOTIFICATION" -> {
                handleDailyNotification(context)
                return
            }
        }

        ensureChannel(context)

        val hour = intent.getIntExtra(EXTRA_HOUR, 0)
        val minute = intent.getIntExtra(EXTRA_MINUTE, 0)
        val musicResId = intent.getIntExtra(EXTRA_MUSIC, -1)
        val title = intent.getStringExtra(EXTRA_TEXT) ?: "Eslatma"

        // Notification Channel
        val channelId = "alarm_channel_id"
        val notificationId = 101
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            channelId,
            "Alarm Channel",
            NotificationManager.IMPORTANCE_HIGH
        ).apply { description = "Alarm notifications" }
        notificationManager.createNotificationChannel(channel)

        // Stop Alarm Intent
        val stopIntent = StopAlarmBroadCast.getIntent(context)
        val stopPendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Build Notification
        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_alarm)
            .setContentTitle(title)
            .setContentText("Soat $hour:$minute bo‘ldi")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .addAction(R.drawable.ic_close, "O‘chirish", stopPendingIntent)

        notificationManager.notify(notificationId, notificationBuilder.build())
        
        // Schedule "Did you pray?" reminder after 15 minutes
        scheduleQazoReminder(context, title)

        // Reschedule alarms if it's Tong or Xufton
        if (title.contains("Bomdod") || title.contains("Xufton")) {
            val workRequest = androidx.work.OneTimeWorkRequestBuilder<uz.coder.muslimcalendar.data.service.PrayerAlarmWorker>().build()
            androidx.work.WorkManager.getInstance(context).enqueue(workRequest)
        }

        // Play Music
        if (musicResId != -1) {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer.create(context, musicResId)
            mediaPlayer?.start()
        }
    }
    private fun handleDailyNotification(context: Context) {
        val hijriDate = java.time.chrono.HijrahDate.now()
        val formatter = java.time.format.DateTimeFormatter.ofPattern("dd MMMM", java.util.Locale.getDefault())
        val hijriStr = hijriDate.format(formatter)

        val channelId = "daily_notification_channel"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        val channel = NotificationChannel(
            channelId,
            "Daily Notifications",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_alarm)
            .setContentTitle("New Islamic Day")
            .setContentText("Today is $hijriStr. Stay mindful.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(202, notification)

        // Schedule next day's notification and update widget
        val workRequest = androidx.work.OneTimeWorkRequestBuilder<uz.coder.muslimcalendar.data.service.PrayerAlarmWorker>().build()
        androidx.work.WorkManager.getInstance(context).enqueue(workRequest)
        
        // Schedule follow-up reminder
        val followUpEnabled = sharedPref.getBoolean("follow_up_enabled", true)
        if (followUpEnabled) {
            val delay = sharedPref.getInt("follow_up_delay", 15)
            val followUpWork = androidx.work.OneTimeWorkRequestBuilder<uz.coder.muslimcalendar.data.service.QazoReminderWorker>()
                .setInitialDelay(delay.toLong(), java.util.concurrent.TimeUnit.MINUTES)
                .setInputData(androidx.work.workDataOf("is_daily_follow_up" to true))
                .build()
            androidx.work.WorkManager.getInstance(context).enqueue(followUpWork)
        }
    }

    private fun scheduleQazoReminder(context: Context, prayerName: String) {
        val workRequest = androidx.work.OneTimeWorkRequestBuilder<uz.coder.muslimcalendar.data.service.QazoReminderWorker>()
            .setInitialDelay(15, java.util.concurrent.TimeUnit.MINUTES)
            .setInputData(androidx.work.workDataOf("prayer_name" to prayerName))
            .build()
        androidx.work.WorkManager.getInstance(context).enqueue(workRequest)
    }

    private fun ensureChannel(context: Context) {
        val manager = context.getSystemService(NotificationManager::class.java)
        if (manager.getNotificationChannel("prayer_alarm_channel") == null) {
            val channel = NotificationChannel(
                "prayer_alarm_channel",
                "Namoz vaqtlari",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Namoz vaqtlari uchun eslatmalar"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 1000, 500, 1000)
                setSound(null, null) // We'll play azan manually
            }
            manager.createNotificationChannel(channel)
        }
    }

}