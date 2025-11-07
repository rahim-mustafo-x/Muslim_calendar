package uz.coder.muslimcalendar.data.service

import android.annotation.SuppressLint
import android.app.job.JobParameters
import android.app.job.JobService
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import uz.coder.muslimcalendar.data.db.AppDatabase
import uz.coder.muslimcalendar.data.map.CalendarMap
import uz.coder.muslimcalendar.data.network.ApiServiceQuranArab
import javax.inject.Inject

@SuppressLint("SpecifyJobSchedulerIdRange")
@AndroidEntryPoint
class QuranJobService: JobService() {

    @Inject lateinit var apiService: ApiServiceQuranArab
    @Inject lateinit var map: CalendarMap
    @Inject lateinit var db: AppDatabase

    private val jobScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onStartJob(params: JobParameters?): Boolean {
        Log.d(TAG, "onStartJob: #kirdi")
        jobScope.launch {
            try {
                getSuraList()
                jobFinished(params, false)
            } catch (e: Exception) {
                e.printStackTrace()
                jobFinished(params, true)
            }
        }
        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        jobScope.coroutineContext.cancelChildren()
        return true
    }

    private suspend fun getSuraList() {
        val response = apiService.getQuranArab()
        if (response.isSuccessful) {
            val data = response.body()?.data?.map { map.toSuraDbModel(it) } ?: emptyList()
            db.suraDao().insertAll(data)
        }
    }

    companion object {
        private const val TAG = "QuranJobService"
    }
}