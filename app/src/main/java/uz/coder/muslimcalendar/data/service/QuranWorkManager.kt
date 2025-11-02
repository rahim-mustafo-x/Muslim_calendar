package uz.coder.muslimcalendar.data.service

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import uz.coder.muslimcalendar.data.db.AppDatabase
import uz.coder.muslimcalendar.data.map.CalendarMap
import uz.coder.muslimcalendar.data.network.ApiServiceQuranArab
import uz.coder.muslimcalendar.todo.isConnected

@HiltWorker
class QuranWorkManager @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val apiService: ApiServiceQuranArab,
    private val map: CalendarMap,
    private val db: AppDatabase
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            if (!context.isConnected()) return Result.retry()

            val existingCount = db.suraDao().getCount()
            if (existingCount == 0) {
                val result = apiService.getQuranArab()
                result.data?.surahs?.let { surahs ->
                    surahs.forEachIndexed { index, surah ->
                        db.suraDao().insertAll(map.toSuraDbModel(listOf(surah)))
                        val progress = ((index + 1) * 100 / surahs.size)
                        setProgress(workDataOf(KEY_PROGRESS to progress))
                    }
                }
            }
            setProgress(workDataOf(KEY_PROGRESS to 100))
            Result.success()
        } catch (_: Exception) {
            Result.retry()
        }
    }

    companion object {
        const val KEY_PROGRESS = "progress"
    }
}