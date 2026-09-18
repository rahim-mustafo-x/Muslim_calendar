package uz.coder.muslimcalendar.data.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import uz.coder.muslimcalendar.domain.repository.SettingsRepository

class PrayerActionReceiver : BroadcastReceiver(), KoinComponent {
    
    private val settingsRepository: SettingsRepository by inject()

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return
        
        val prayerName = intent.getStringExtra("prayer_name") ?: return
        val eventId = intent.getStringExtra("event_id") ?: return
        val notificationId = intent.getIntExtra("notification_id", -1)
        
        if (intent.action == "ACTION_PRAYER_YES") {
            CoroutineScope(Dispatchers.IO).launch {
                settingsRepository.recordPrayerResponse(prayerName, prayed = true, eventId = eventId)
            }
        } else if (intent.action == "ACTION_PRAYER_NO") {
            CoroutineScope(Dispatchers.IO).launch {
                settingsRepository.recordPrayerResponse(prayerName, prayed = false, eventId = eventId)
            }
        }
        
        if (notificationId != -1) {
            NotificationManagerCompat.from(context).cancel(notificationId)
        }
    }
}
