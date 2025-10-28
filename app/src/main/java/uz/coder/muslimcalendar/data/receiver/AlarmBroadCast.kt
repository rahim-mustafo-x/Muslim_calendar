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
import uz.coder.muslimcalendar.R

class AlarmBroadCast : BroadcastReceiver() {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent != null) {
            showNotificationAndPlaySound(context, intent)
        }
    }

    private fun showNotificationAndPlaySound(context: Context, intent: Intent) {
        val hour = intent.getIntExtra(EXTRA_HOUR, 0)
        val minute = intent.getIntExtra(EXTRA_MINUTE, 0)
        val musicResId = intent.getIntExtra(EXTRA_MUSIC, -1)
        val title = intent.getStringExtra(EXTRA_TEXT) ?: "Eslatma"

        val channelId = "alarm_channel_id"
        val notificationId = 101

        val stopIntent = StopAlarmBroadCast.getIntent(context)
        val stopPendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Faqat Android 8.0+ uchun kanal yaratish
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                channelId,
                "Alarm Channel",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alarm notifications"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_alarm)
            .setContentTitle(title)
            .setContentText("Soat $hour:$minute bo‘ldi")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .addAction(R.drawable.ic_close, "O‘chirish", stopPendingIntent)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (musicResId == R.raw.azan || musicResId == -2) {
            notificationManager.notify(notificationId, notificationBuilder.build())
        }

        if (musicResId == R.raw.azan) {
            mediaPlayer?.release() // Eski playerni tozalash
            mediaPlayer = MediaPlayer.create(context, musicResId)
            mediaPlayer?.start()
        }
    }

    companion object {
        var mediaPlayer: MediaPlayer? = null

        fun getIntent(context: Context, hour: Int, minute: Int, text: String, musicResId: Int): Intent {
            return Intent(context, AlarmBroadCast::class.java).apply {
                putExtra(EXTRA_TEXT, text)
                putExtra(EXTRA_HOUR, hour)
                putExtra(EXTRA_MINUTE, minute)
                putExtra(EXTRA_MUSIC, musicResId)
            }
        }

        private const val EXTRA_TEXT = "extra_text"
        private const val EXTRA_HOUR = "extra_hour"
        private const val EXTRA_MINUTE = "extra_minute"
        private const val EXTRA_MUSIC = "extra_music"
    }
}
