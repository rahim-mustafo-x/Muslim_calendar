package uz.coder.muslimcalendar.broadcast

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.media.MediaPlayer
import androidx.core.app.NotificationCompat
import uz.coder.muslimcalendar.R
import uz.coder.muslimcalendar.todo.App
import uz.coder.muslimcalendar.todo.CHANNEL_ID
import uz.coder.muslimcalendar.todo.CHANNEL_NAME
import uz.coder.muslimcalendar.todo.ID
import uz.coder.muslimcalendar.todo.ITEM_NAME
import uz.coder.muslimcalendar.todo.ITEM_TIME

class AlarmBroadCast:BroadcastReceiver() {
    private val manager = App.getApplication().getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    override fun onReceive(context: Context?, intent: Intent?) {
        val mediaPlayer = MediaPlayer.create(context, R.raw.azan)
        mediaPlayer.start()
        context?.let {
            createChannel()
            val notification = NotificationCompat.Builder(it, CHANNEL_ID)
                .setSmallIcon(R.drawable.time)
                .setContentTitle(intent?.getStringExtra(ITEM_NAME)?.plus(it.getString(R.string.prayerTimeName))?:"")
                .setContentText(String.format(it.getString(R.string.prayerTime), intent?.getStringExtra(
                    ITEM_TIME)))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build()
            manager.notify(ID, notification)
        }
    }

    private fun createChannel() {
        val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
        manager.createNotificationChannel(channel)
    }
}