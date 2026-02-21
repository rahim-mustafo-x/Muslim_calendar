package uz.coder.muslimcalendar

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.HiltAndroidApp
import uz.coder.muslimcalendar.data.service.PrayerAlarmWorker
import javax.inject.Inject

@HiltAndroidApp
class App : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        triggerPrayerAlarmWorker()
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            "prayer_alarm_channel",
            "Prayer Alarms",
            NotificationManager.IMPORTANCE_HIGH
        )
        channel.description = "Prayer alarm notifications"
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    private fun triggerPrayerAlarmWorker() {
        val request = OneTimeWorkRequestBuilder<PrayerAlarmWorker>().build()
        WorkManager.getInstance(this)
            .enqueueUniqueWork(
                "PRAYER_ALARM_WORK",
                ExistingWorkPolicy.REPLACE,
                request
            )
    }
}