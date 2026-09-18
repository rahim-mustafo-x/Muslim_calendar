package uz.coder.muslimcalendar.data.service

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import uz.coder.muslimcalendar.domain.repository.NotificationScheduler
import uz.coder.muslimcalendar.domain.repository.SettingsRepository
import uz.coder.muslimcalendar.domain.repository.CalendarRepository
import uz.coder.muslimcalendar.SharedPref
import uz.coder.muslimcalendar.todo.CALENDAR_DOWNLOAD_ALLOWED
import uz.coder.muslimcalendar.todo.CALENDAR_DOWNLOAD_CONSENT
import uz.coder.muslimcalendar.todo.LOCATION_CONFIGURED
import uz.coder.muslimcalendar.todo.SAVED_LATITUDE
import uz.coder.muslimcalendar.todo.SAVED_LONGITUDE

class PrayerAlarmWorker(
    context: Context,
    params: WorkerParameters,
    private val scheduler: NotificationScheduler,
    private val settingsRepository: SettingsRepository,
    private val calendarRepository: CalendarRepository,
    private val sharedPref: SharedPref
) : CoroutineWorker(context, params) {

    companion object {
        private const val TAG = "PrayerAlarmWorker"
    }

    override suspend fun doWork(): Result {
        return try {
            Log.d(TAG, "Starting prayer alarm rescheduling and daily check")
            settingsRepository.checkAndResetDailyPrayers()
            if (sharedPref.getString(CALENDAR_DOWNLOAD_CONSENT) == CALENDAR_DOWNLOAD_ALLOWED &&
                sharedPref.getBoolean(LOCATION_CONFIGURED)
            ) {
                calendarRepository.loading(
                    longitude = sharedPref.getFloat(SAVED_LONGITUDE).toDouble(),
                    latitude = sharedPref.getFloat(SAVED_LATITUDE).toDouble()
                )
            }
            scheduler.rescheduleAll()
            Log.d(TAG, "Prayer alarm rescheduling completed successfully")
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Error rescheduling prayer alarms", e)
            Result.retry()
        }
    }
}
