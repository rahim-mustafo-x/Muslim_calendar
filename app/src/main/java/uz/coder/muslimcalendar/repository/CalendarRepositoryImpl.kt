package uz.coder.muslimcalendar.repository

import android.app.Application
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.SharedPreferences
import android.os.PersistableBundle
import android.util.Log
import androidx.core.content.edit
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import uz.coder.muslimcalendar.R
import uz.coder.muslimcalendar.db.AppDatabase
import uz.coder.muslimcalendar.ktor.ApiService.Companion.apiService
import uz.coder.muslimcalendar.map.CalendarMap
import uz.coder.muslimcalendar.models.model.quran.Sura
import uz.coder.muslimcalendar.models.model.quran.Surah
import uz.coder.muslimcalendar.service.PrayerTimeJobService
import uz.coder.muslimcalendar.service.QuranWorkManager
import uz.coder.muslimcalendar.todo.REGION
import java.time.LocalDate
import java.util.Calendar.DAY_OF_MONTH
import java.util.Calendar.MONTH
import java.util.Calendar.getInstance
import java.util.concurrent.TimeUnit

data class CalendarRepositoryImpl(private val application: Application):CalendarRepository {
    private val preferences:SharedPreferences by lazy { application.getSharedPreferences(application.getString(R.string.app_name), Context.MODE_PRIVATE) }
    private val db:AppDatabase by lazy { AppDatabase.instance(application) }
    private val map = CalendarMap()

    override suspend fun loading(longitude: Double, latitude: Double) {
        if (latitude!=0.0 && longitude !=0.0){
            val extras = PersistableBundle().apply {
                putDouble(PrayerTimeJobService.LATITUDE, latitude)
                putDouble(PrayerTimeJobService.LONGITUDE, longitude)
            }

            val jobInfo = JobInfo.Builder(1, ComponentName(application, PrayerTimeJobService::class.java))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setExtras(extras)
                .setOverrideDeadline(0) // run immediately
                .build()

            val scheduler = application.getSystemService(JobScheduler::class.java)
            scheduler.schedule(jobInfo)
        }
    }


    override suspend fun region(region: String) {
        preferences.edit { putString(REGION, region) }
    }

    override suspend fun remove() {
        val calendar = getInstance()
        val month = calendar.get(MONTH)+1
        val day = calendar.get(DAY_OF_MONTH)
        db.calendarDao().refreshDay().collect{
            if (it.day == day && it.month == month){
                db.calendarDao().deleteCalendar()
            }
        }
    }

    override fun presentDay() = flow {
        val localeDate = LocalDate.now()
        val presentDay =
            db.calendarDao().presentDay(localeDate.dayOfMonth, localeDate.monthValue)
           presentDay.collect{
               Log.d(TAG, "presentDay: $it")
                   try {
                       emit(
                           map.toMuslimCalendar(it)
                       )
                   }catch (_:Exception){}
           }
    }

    override fun oneMonth() = channelFlow {
        db.calendarDao().oneMonth().collect{
            send(map.toMuslimCalendarList(it))
        }
    }

    override suspend fun loadQuranArab() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<QuranWorkManager>()
            .setConstraints(constraints)
            .setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(application)
            .enqueueUniqueWork("QuranWork", ExistingWorkPolicy.KEEP, workRequest)

        // Observe the work status
        WorkManager.getInstance(application)
            .getWorkInfoByIdLiveData(workRequest.id)
            .observeForever { workInfo ->
                when (workInfo?.state) {
                    WorkInfo.State.SUCCEEDED -> Log.d(TAG, "Work completed successfully")
                    WorkInfo.State.FAILED -> Log.d(TAG, "Work failed")
                    WorkInfo.State.CANCELLED -> Log.d(TAG, "Work cancelled")
                    else -> Log.d(TAG, "Work state: ${workInfo?.state}")
                }
            }
    }

    override fun getSurah() = flow<List<Sura>> {
        db.suraDao().getAllSura().collect{
            emit(
                map.toSuraList(it)
            )
        }
    }

    override fun getSurahByNumber(number: Int) = flow<Sura> {
        db.suraDao().getSuraById(number).collect{
            emit(
                map.toSura(it)
            )
        }
    }

    override fun getSura(surahNumber: Int) = flow<Surah> {
        val result = withContext(Dispatchers.IO) { apiService.getSura(surahNumber) }
        emit(
            Surah(
                map.toSurahList(result.result)
            )
        )
    }
}

private const val TAG = "CalendarRepositoryImpl"