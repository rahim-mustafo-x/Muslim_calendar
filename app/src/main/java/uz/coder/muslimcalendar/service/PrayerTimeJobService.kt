package uz.coder.muslimcalendar.service

import android.annotation.SuppressLint
import android.app.job.JobParameters
import android.app.job.JobService
import android.util.Log
import kotlinx.coroutines.*
import uz.coder.muslimcalendar.db.AppDatabase
import uz.coder.muslimcalendar.ktor.PrayerTimeService
import uz.coder.muslimcalendar.map.CalendarMap
import java.io.IOException

@SuppressLint("SpecifyJobSchedulerIdRange")
class PrayerTimeJobService : JobService() {
    private val job = SupervisorJob()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + job)
    private val db:AppDatabase by lazy { AppDatabase.instance(application) }
    private val map = CalendarMap()

    override fun onStartJob(params: JobParameters?): Boolean {
        val latitude = params?.extras?.getDouble(LATITUDE, 0.0) ?: 0.0
        val longitude = params?.extras?.getDouble(LONGITUDE, 0.0) ?: 0.0

        coroutineScope.launch {
            loading(longitude, latitude)
            jobFinished(params, false)
        }

        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        job.cancel()
        return true
    }

    private suspend fun loading(longitude: Double, latitude: Double) {
        Log.d(TAG, "loading: $longitude/$latitude")
        if (longitude != 0.0 && latitude != 0.0) {
            try {
                val result = withContext(Dispatchers.IO) {
                    PrayerTimeService.oneMonth.oneMonth(latitude, longitude)
                }
                result.data?.let {
                    db.calendarDao().insertMuslimCalendar(map.toMuslimCalendarDbModel(it))
                    Log.d(TAG, "loading: $it")
                } ?: Log.d(TAG, "loading: bo'sh")
            } catch (e: IOException) {
                Log.e(TAG, "Tarmoq xatoligi", e)
            } catch (e: Exception) {
                Log.e(TAG, "Noma'lum xatolik", e)
            }
        } else {
            Log.d(TAG, "loading: koordinatalar bo'sh")
        }
    }
    companion object{
        private const val TAG = "PrayerTimeJobService"
        const val LONGITUDE = "longitude"
        const val LATITUDE = "latitude"
    }
}