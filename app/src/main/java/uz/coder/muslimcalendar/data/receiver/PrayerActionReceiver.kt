package uz.coder.muslimcalendar.data.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import uz.coder.muslimcalendar.domain.repository.SettingsRepository

class PrayerActionReceiver : BroadcastReceiver(), KoinComponent {
    private val settingsRepository: SettingsRepository by inject()

    @OptIn(DelicateCoroutinesApi::class)
    override fun onReceive(context: Context?, intent: Intent?) {
        val prayerName = intent?.getStringExtra("EXTRA_PRAYER_NAME") ?: return
        val eventId = intent.getStringExtra("EXTRA_EVENT_ID") ?: return
        val prayed = intent.getBooleanExtra("EXTRA_PRAYED", false)
        val notificationId = intent.getIntExtra("EXTRA_NOTIFICATION_ID", 101)

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

        // Update Statistics
        GlobalScope.launch {
            settingsRepository.recordPrayerResponse(prayerName, prayed, eventId)
        }
    }
}
