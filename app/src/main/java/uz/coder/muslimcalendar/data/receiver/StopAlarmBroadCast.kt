package uz.coder.muslimcalendar.data.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StopAlarmBroadCast : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        // Stop MediaPlayer
        AlarmBroadCast.mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
                it.release()
            }
        }
        AlarmBroadCast.mediaPlayer = null

        // Cancel Notification
        val notificationManager =
            context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(101)
    }

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, StopAlarmBroadCast::class.java)
        }
    }
}