package uz.coder.muslimcalendar.data.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import uz.coder.muslimcalendar.domain.repository.SettingsRepository

class PrayerActionReceiver : BroadcastReceiver(), KoinComponent {
    private val settingsRepository: SettingsRepository by inject()

    override fun onReceive(context: Context?, intent: Intent?) {
        val prayerName = intent?.getStringExtra("EXTRA_PRAYER_NAME") ?: intent?.getStringExtra("prayer_name") ?: return
        val eventId = intent?.getStringExtra("EXTRA_EVENT_ID") ?: intent?.getStringExtra("event_id") ?: return
        val prayed = intent?.getBooleanExtra("EXTRA_PRAYED", false) == true || (intent?.action == "ACTION_PRAYER_YES")
        val notificationId = intent?.getIntExtra("EXTRA_NOTIFICATION_ID", intent?.getIntExtra("notification_id", 101) ?: 101) ?: 101

        // Stop Azan
        AlarmBroadCast.mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
                it.release()
            }
        }
        AlarmBroadCast.mediaPlayer = null

        // Cancel Notification
        val notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        notificationManager?.cancel(notificationId)

        // Update Statistics asynchronously without blocking receiver
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                settingsRepository.recordPrayerResponse(prayerName, prayed, eventId)
            } finally {
                pendingResult.finish()
            }
        }
    }
}
