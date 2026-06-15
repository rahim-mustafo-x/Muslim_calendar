package uz.coder.muslimcalendar.data.service

import android.annotation.SuppressLint
import android.app.job.JobParameters
import android.app.job.JobService
import android.util.Log
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlinx.coroutines.*
import uz.coder.muslimcalendar.data.db.AppDatabase
import uz.coder.muslimcalendar.data.map.CalendarMap
import uz.coder.muslimcalendar.data.network.KtorApiService

@SuppressLint("SpecifyJobSchedulerIdRange")
class QuranJobService : JobService(), KoinComponent {

    private val apiService: KtorApiService by inject()
    private val map: CalendarMap by inject()
    private val db: AppDatabase by inject()

    private val jobScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    companion object {
        private const val TAG = "QuranJobService"
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartJob(params: JobParameters?): Boolean {
        if (params == null) return false

        jobScope.launch {
            try {
                getSuraList()
                jobFinished(params, false)
            } catch (e: Exception) {
                Log.e(TAG, "Error in QuranJobService", e)
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
        Log.d(TAG, "getSuraList: start")

        try {
            val response = apiService.getQuranArab()
            Log.d(TAG, "Response code = ${response.code}")
            Log.d(TAG, "Response status = ${response.status}")
            Log.d(TAG, "Response data = ${response.data}")

            val data = response.data
            if (data != null) {
                val dbModels = data.map { map.toSuraDbModel(it) }
                db.suraDao().insertAll(dbModels)
                Log.d(TAG, "getSuraList: Saved to DB (${dbModels.size} items)")
            } else {
                Log.e(TAG, "getSuraList: API returned null data")
            }
        } catch (e: Exception) {
            Log.e(TAG, "getSuraList: error in quran obtaining", e)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        jobScope.cancel()
    }
}
