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

        // Play Music
        if (musicResId != -1) {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer.create(context, musicResId)
            mediaPlayer?.start()
        }
    }
    private fun ensureChannel(context: Context) {
        val manager = context.getSystemService(NotificationManager::class.java)
        if (manager.getNotificationChannel("alarm_channel_id") == null) {
            manager.createNotificationChannel(
                NotificationChannel(
                    "alarm_channel_id",
                    "Namoz alarmlari",
                    NotificationManager.IMPORTANCE_HIGH
                )
            )
        }
    }

}