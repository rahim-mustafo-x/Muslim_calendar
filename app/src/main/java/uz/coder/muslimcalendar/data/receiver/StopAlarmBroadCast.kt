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
        val notificationId = intent?.getIntExtra(EXTRA_NOTIFICATION_ID, 101) ?: 101
        val notificationManager =
            context?.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        notificationManager?.cancel(notificationId)
    }

    companion object {
        private const val EXTRA_NOTIFICATION_ID = "notification_id"
        
        fun getIntent(context: Context, notificationId: Int = 101): Intent {
            return Intent(context, StopAlarmBroadCast::class.java).apply {
                putExtra(EXTRA_NOTIFICATION_ID, notificationId)
            }
        }
    }
}