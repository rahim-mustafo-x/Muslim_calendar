package uz.coder.muslimcalendar.data.service

import android.annotation.SuppressLint
import android.app.job.JobParameters
import android.app.job.JobService
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import uz.coder.muslimcalendar.data.db.AppDatabase
import uz.coder.muslimcalendar.data.map.CalendarMap
import uz.coder.muslimcalendar.data.network.ApiServicePrayerTime
import java.io.IOException
import java.time.LocalDate
import javax.inject.Inject

@SuppressLint("SpecifyJobSchedulerIdRange")
@AndroidEntryPoint
class PrayerTimeJobService : JobService() {

    private val job = SupervisorJob()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + job)

    @Inject
    lateinit var db: AppDatabase

    @Inject
    lateinit var map: CalendarMap

    @Inject
    lateinit var apiService: ApiServicePrayerTime

    override fun onStartJob(params: JobParameters?): Boolean {
        val latitude = params?.extras?.getDouble(LATITUDE, 0.0) ?: 0.0
        val longitude = params?.extras?.getDouble(LONGITUDE, 0.0) ?: 0.0

        if (latitude == 0.0 || longitude == 0.0) {
            Log.w(TAG, "Koordinatalar bo'sh, job tugatiladi")
            jobFinished(params, false)
            return false
        }

        coroutineScope.launch {
            try {
                loadPrayerTimes(latitude, longitude)
            } catch (e: Exception) {
                Log.e(TAG, "Noma’lum xatolik", e)
            } finally {
                jobFinished(params, false)
            }
        }

        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        job.cancel()
        return true
    }

    private suspend fun loadPrayerTimes(latitude: Double, longitude: Double) {
        val localDate = LocalDate.now()
        val year = localDate.year
        val month = localDate.month.value
        Log.d(TAG, "loading: $latitude/$longitude for $year-$month")

        try {
            val result = apiService.oneMonth(year, month, latitude, longitude)
            result.data?.let {
                db.calendarDao().insertMuslimCalendar(map.toMuslimCalendarDbModel(it))
                Log.d(TAG, "Namoz vaqtlari bazaga saqlandi")
            } ?: Log.w(TAG, "API dan data bo'sh qaytdi")
        } catch (e: IOException) {
            Log.e(TAG, "Tarmoq xatoligi", e)
        } catch (e: Exception) {
            Log.e(TAG, "Noma’lum xatolik", e)
        }
    }

    companion object {
        private const val TAG = "PrayerTimeJobService"
        const val LONGITUDE = "longitude"
        const val LATITUDE = "latitude"
    }
}