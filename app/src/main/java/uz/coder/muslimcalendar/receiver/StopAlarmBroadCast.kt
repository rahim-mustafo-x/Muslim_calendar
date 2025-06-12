package uz.coder.muslimcalendar.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class StopAlarmBroadCast : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        // MediPlayer obyektini global qilish kerak yoki boshqa usulda to'xtatish kerak
        AlarmBroadCast.mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
                it.release()
            }
        }
        AlarmBroadCast.mediaPlayer = null

        // Notificationni o'chirish
        val notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(101) // AlarmBroadCast ichida notificationId = 101 edi
    }

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, StopAlarmBroadCast::class.java)
        }
    }
}
