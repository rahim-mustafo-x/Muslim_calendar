package uz.coder.muslimcalendar.data.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import androidx.work.WorkManager
import androidx.work.workDataOf
import java.util.concurrent.TimeUnit
import uz.coder.muslimcalendar.R
import uz.coder.muslimcalendar.data.receiver.PrayerActionReceiver
import uz.coder.muslimcalendar.domain.repository.SettingsRepository

class QazoReminderWorker(
    private val context: Context,
    params: WorkerParameters,
    private val settingsRepository: SettingsRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val prayerName = inputData.getString("prayer_name") ?: return Result.failure()
        val eventId = inputData.getString("event_id") ?: return Result.failure()
        if (inputData.getBoolean("mark_missed", false)) {
            settingsRepository.recordPrayerResponse(prayerName, prayed = false, eventId = eventId)
        } else showReminderNotification(prayerName, eventId)
        
        return Result.success()
    }

    private fun showReminderNotification(prayerName: String, eventId: String) {
        val channelId = "prayer_check_channel"
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            channelId,
            "Namoz holati",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)

        val notificationId = eventId.hashCode()
        
        // Yes Intent
        val yesIntent = android.content.Intent(context, uz.coder.muslimcalendar.data.receiver.PrayerActionReceiver::class.java).apply {
            action = "ACTION_PRAYER_YES"
            putExtra("prayer_name", prayerName)
            putExtra("event_id", eventId)
            putExtra("notification_id", notificationId)
        }
        val yesPendingIntent = android.app.PendingIntent.getBroadcast(
            context,
            notificationId,
            yesIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // No Intent
        val noIntent = Intent(context, PrayerActionReceiver::class.java).apply {
            action = "ACTION_PRAYER_NO"
            putExtra("prayer_name", prayerName)
            putExtra("event_id", eventId)
            putExtra("notification_id", notificationId)
        }
        val noPendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId + 1,
            noIntent,
            android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_alarm)
            .setContentTitle("Namoz eslatmasi")
            .setContentText("$prayerName o'qidingizmi?")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .addAction(0, "Ha", yesPendingIntent)
            .addAction(0, "Yo'q", noPendingIntent)
            .build()

        notificationManager.notify(notificationId, notification)

        // If the user does not answer within the response window, count it as missed.
        val expiry = OneTimeWorkRequestBuilder<QazoReminderWorker>()
            .setInitialDelay(30, TimeUnit.MINUTES)
            .setInputData(workDataOf(
                "prayer_name" to prayerName,
                "event_id" to eventId,
                "mark_missed" to true
            ))
            .build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            "prayer-response-expiry-$eventId", ExistingWorkPolicy.REPLACE, expiry
        )
    }
}
