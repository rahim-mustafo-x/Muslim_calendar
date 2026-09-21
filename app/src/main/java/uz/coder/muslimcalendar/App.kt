package uz.coder.muslimcalendar

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.work.Configuration
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import uz.coder.muslimcalendar.data.service.PrayerAlarmWorker
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.GlobalContext.startKoin
import uz.coder.muslimcalendar.di.appModule

class App : Application(), Configuration.Provider {

    override fun onCreate() {
        super.onCreate()

        // Call startKoin ONLY once
        startKoin {
            androidContext(this@App)
            workManagerFactory()
            modules(appModule)
        }

        createNotificationChannel()
        triggerPrayerAlarmWorker()
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(org.koin.androidx.workmanager.factory.KoinWorkerFactory())
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