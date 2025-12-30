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
        if (params == null) return false

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
        Log.d(TAG, "getSuraList: start")

        try {
            val response = apiService.getQuranArab()
            Log.d(TAG, "code = ${response.code()}")
            Log.d(TAG, "isSuccessful = ${response.isSuccessful}")
            Log.d(TAG, "errorBody = ${response.errorBody()?.string()}")
            Log.d(TAG, "body = ${response.body()}")

            if (response.isSuccessful && response.body() != null) {
                val data = response.body()!!.data
                val dbModels = data?.map { map.toSuraDbModel(it) }?:emptyList()
                db.suraDao().insertAll(dbModels)
                Log.d(TAG, "getSuraList: DB ga saqlandi (${dbModels.size})")
            } else {
                Log.e(TAG, "getSuraList: API xato bilan qaytdi")
            }
        }catch (e: Exception){
            Log.e(TAG, "getSuraList: error in quran obtaining", e)
        }
    }

    companion object {
        private const val TAG = "QuranJobService"
    }
}