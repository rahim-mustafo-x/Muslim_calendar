package uz.coder.muslimcalendar.data.service

import android.content.Context
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
    override suspend fun doWork(): Result {
        return try {
            scheduler.rescheduleAll()
            Result.success()
        } catch (_: Exception) {
            Result.retry()
        }
    }
}