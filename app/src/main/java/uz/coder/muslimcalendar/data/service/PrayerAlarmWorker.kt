package uz.coder.muslimcalendar.data.service

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import uz.coder.muslimcalendar.domain.repository.NotificationScheduler

@HiltWorker
class PrayerAlarmWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val scheduler: NotificationScheduler
) : CoroutineWorker(context, params) {

    companion object {
        private const val TAG = "PrayerAlarmWorker"
    }

    override suspend fun doWork(): Result {
        return try {
            Log.d(TAG, "Starting prayer alarm rescheduling")
            scheduler.rescheduleAll()
            Log.d(TAG, "Prayer alarm rescheduling completed successfully")
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Error rescheduling prayer alarms", e)
            Result.retry()
        }
    }
}
