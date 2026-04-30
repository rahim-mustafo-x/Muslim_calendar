package uz.coder.muslimcalendar.data.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import uz.coder.muslimcalendar.R

@HiltWorker
class QazoReminderWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val isDailyFollowUp = inputData.getBoolean("is_daily_follow_up", false)
        if (isDailyFollowUp) {
            showDailyFollowUpNotification()
        } else {
            val prayerName = inputData.getString("prayer_name") ?: "Namoz"
            showReminderNotification(prayerName)
        }
        
        return Result.success()
    }

    private fun showDailyFollowUpNotification() {
        val channelId = "qazo_reminder_channel"
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_alarm)
            .setContentTitle("Reminder")
            .setContentText("Did you pray?")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(203, notification)
    }

    private fun showReminderNotification(prayerName: String) {
        val channelId = "qazo_reminder_channel"
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            channelId,
            "Qazo Eslatmalari",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)

        val notificationId = prayerName.hashCode()
        
        val yesIntent = android.content.Intent(context, uz.coder.muslimcalendar.data.receiver.PrayerActionReceiver::class.java).apply {
            action = "ACTION_PRAYER_YES"
            putExtra("prayer_name", prayerName)
            putExtra("notification_id", notificationId)
        }
        val yesPendingIntent = android.app.PendingIntent.getBroadcast(
            context,
            notificationId,
            yesIntent,
            android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_alarm)
            .setContentTitle("Namoz eslatmasi")
            .setContentText("$prayerName o'qidingizmi?")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .addAction(0, "Ha", yesPendingIntent)
            .build()

        notificationManager.notify(notificationId, notification)
    }
}
