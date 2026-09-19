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
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import uz.coder.muslimcalendar.R
import uz.coder.muslimcalendar.data.service.PrayerAlarmWorker
import uz.coder.muslimcalendar.data.service.QazoReminderWorker
import java.time.chrono.HijrahDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoField
import java.util.Locale
import java.util.concurrent.TimeUnit

class AlarmBroadCast : BroadcastReceiver() {

    companion object {
        var mediaPlayer: MediaPlayer? = null

        private const val EXTRA_TEXT = "extra_text"
        private const val EXTRA_HOUR = "extra_hour"
        private const val EXTRA_MINUTE = "extra_minute"
        private const val EXTRA_MUSIC = "extra_music"
        private const val EXTRA_EVENT_ID = "extra_event_id"

        fun getIntent(context: Context, hour: Int, minute: Int, text: String, musicResId: Int, eventId: String): Intent {
            return Intent(context, AlarmBroadCast::class.java).apply {
                putExtra(EXTRA_TEXT, text)
                putExtra(EXTRA_HOUR, hour)
                putExtra(EXTRA_MINUTE, minute)
                putExtra(EXTRA_MUSIC, musicResId)
                putExtra(EXTRA_EVENT_ID, eventId)
            }
        }
    }

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return
        
        when (intent.action) {
            "ACTION_REFRESH_ALARMS" -> {
                val workRequest = OneTimeWorkRequestBuilder<PrayerAlarmWorker>().build()
                WorkManager.getInstance(context).enqueue(workRequest)
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
        val eventId = intent.getStringExtra(EXTRA_EVENT_ID) ?: return

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "prayer_alarm_channel"
        val notificationId = 101

        // Custom XML Custom remote views layout implementation for Azan notification popup banner
        val customLayout = RemoteViews(context.packageName, R.layout.notification_custom_azan).apply {
            setTextViewText(R.id.notification_title, title)
            setTextViewText(R.id.notification_text, "Soat $hour:$minute bo‘ldi. Namoz vaqti kirdi.")
        }

        // Stop Action Intent setup
        val stopIntent = StopAlarmBroadCast.getIntent(context)
        val stopPendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        customLayout.setOnClickPendingIntent(R.id.btn_stop_azan, stopPendingIntent)

        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_alarm)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(customLayout)
            .setCustomBigContentView(customLayout)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)

        notificationManager.notify(notificationId, notificationBuilder.build())
        
        schedulePrayerCheck(context, title, eventId)

        if (title.contains("Bomdod") || title.contains("Xufton")) {
            val workRequest = OneTimeWorkRequestBuilder<PrayerAlarmWorker>().build()
            WorkManager.getInstance(context).enqueue(workRequest)
        }

        // Play Azan Audio Audio handling with notification modes option filter override support
        if (musicResId != -1) {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer.create(context, musicResId)
            mediaPlayer?.start()
        }
    }

    private fun handleDailyNotification(context: Context) {
        val hijriDate = HijrahDate.now()
        val hijriStr = hijriDate.toString()
        val parts = hijriStr.split(" ")
        val datePart = parts.lastOrNull() ?: ""
        val dateParts = datePart.split("-")
        
        val monthNumber = dateParts.getOrNull(1)?.toIntOrNull() ?: hijriDate.get(ChronoField.MONTH_OF_YEAR)
        val hijriDay = dateParts.getOrNull(2)?.toIntOrNull() ?: hijriDate.get(ChronoField.DAY_OF_MONTH)

        val hijriMonth = when (monthNumber) {
            1 -> "Muharram"
            2 -> "Safar"
            3 -> "Rabiul avval"
            4 -> "Rabius sani"
            5 -> "Jumadil avval"
            6 -> "Jumadis sani"
            7 -> "Rajab"
            8 -> "Sha’bon"
            9 -> "Ramazon"
            10 -> "Shawvol"
            11 -> "Zulqa’da"
            12 -> "Zulhijja"
            else -> "Muharram"
        }
        val displayHijri = "$hijriDay $hijriMonth"

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
            .setContentText("Bugun: $displayHijri. Yoqimli kun tilaymiz.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(202, notification)

        val workRequest = OneTimeWorkRequestBuilder<PrayerAlarmWorker>().build()
        WorkManager.getInstance(context).enqueue(workRequest)
    }

    private fun schedulePrayerCheck(context: Context, prayerName: String, eventId: String) {
        val workRequest = OneTimeWorkRequestBuilder<QazoReminderWorker>()
            .setInitialDelay(30, TimeUnit.MINUTES)
            .setInputData(workDataOf("prayer_name" to prayerName, "event_id" to eventId))
            .build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            "prayer-check-$eventId",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
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
                setSound(null, null)
            }
            manager.createNotificationChannel(channel)
        }
    }
}
