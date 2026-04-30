package uz.coder.muslimcalendar.data.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uz.coder.muslimcalendar.domain.repository.SettingsRepository
import javax.inject.Inject

@AndroidEntryPoint
class PrayerActionReceiver : BroadcastReceiver() {
    
    @Inject
    lateinit var settingsRepository: SettingsRepository

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return
        
        val prayerName = intent.getStringExtra("prayer_name") ?: return
        val notificationId = intent.getIntExtra("notification_id", -1)
        
        if (intent.action == "ACTION_PRAYER_YES") {
            CoroutineScope(Dispatchers.IO).launch {
                settingsRepository.markPrayerCompleted(prayerName, true)
            }
        }
        
        if (notificationId != -1) {
            NotificationManagerCompat.from(context).cancel(notificationId)
        }
    }
}
