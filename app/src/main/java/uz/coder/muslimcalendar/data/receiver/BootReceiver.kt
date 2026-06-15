package uz.coder.muslimcalendar.data.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import uz.coder.muslimcalendar.domain.repository.NotificationScheduler

class BootReceiver : BroadcastReceiver(), KoinComponent {
    
    private val scheduler: NotificationScheduler by inject()
    
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            CoroutineScope(Dispatchers.Main).launch {
                scheduler.scheduleAllAlarms() // Telefon qayta yoqilganda alarmlarni tiklash
            }
        }
    }
}