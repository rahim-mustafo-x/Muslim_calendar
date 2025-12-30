package uz.coder.muslimcalendar.data.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uz.coder.muslimcalendar.di.BootReceiverEntryPoint

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val entryPoint = EntryPointAccessors.fromApplication(
                context.applicationContext,
                BootReceiverEntryPoint::class.java
            )

            val scheduler = entryPoint.getNotificationScheduler()

            CoroutineScope(Dispatchers.Main).launch {
                scheduler.scheduleAllAlarms() // Telefon qayta yoqilganda alarmlarni tiklash
            }
        }
    }
}