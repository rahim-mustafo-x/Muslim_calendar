package uz.coder.muslimcalendar.data.repository

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.os.PersistableBundle
import android.util.Log
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import uz.coder.muslimcalendar.SharedPref
import uz.coder.muslimcalendar.data.db.AppDatabase
import uz.coder.muslimcalendar.data.map.CalendarMap
import uz.coder.muslimcalendar.data.network.ApiServiceQuranUzbek
import uz.coder.muslimcalendar.data.service.DownloadJobService
import uz.coder.muslimcalendar.data.service.PrayerTimeJobService
import uz.coder.muslimcalendar.data.service.QuranJobService
import uz.coder.muslimcalendar.domain.model.AudioPath
import uz.coder.muslimcalendar.domain.repository.CalendarRepository
import uz.coder.muslimcalendar.models.model.quran.Surah
import uz.coder.muslimcalendar.domain.model.quran.SurahList
import uz.coder.muslimcalendar.todo.REGION
import java.time.LocalDate
import java.util.Calendar.DAY_OF_MONTH
import java.util.Calendar.MONTH
import java.util.Calendar.getInstance
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
data class CalendarRepositoryImpl @Inject constructor(
    private val preferences: SharedPref,
    private val db: AppDatabase,
    private val map: CalendarMap,
    @ApplicationContext private val context: Context,
    private val apiService: ApiServiceQuranUzbek,
    private val gson: Gson
) : CalendarRepository {

    override suspend fun loading(longitude: Double, latitude: Double) {
        if (latitude != 0.0 && longitude != 0.0) {
            val extras = PersistableBundle().apply {
                putDouble(PrayerTimeJobService.LATITUDE, latitude)
                putDouble(PrayerTimeJobService.LONGITUDE, longitude)
            }

            val jobInfo = JobInfo.Builder(1, ComponentName(context, PrayerTimeJobService::class.java))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setExtras(extras)
                .setOverrideDeadline(0)
                .build()

            val scheduler = context.getSystemService(JobScheduler::class.java)
            scheduler.schedule(jobInfo)
        }
    }

    override suspend fun region(region: String) {
        preferences.saveValue(REGION, region)
    }

    override suspend fun remove() {
        val calendar = getInstance()
        val month = calendar.get(MONTH) + 1
        val day = calendar.get(DAY_OF_MONTH)
        db.calendarDao().refreshDay().collect {
            if (it.day == day && it.month == month) {
                db.calendarDao().deleteCalendar()
            }
        }
    }

    override fun presentDay() =
        db.calendarDao().presentDay(LocalDate.now().dayOfMonth, LocalDate.now().monthValue)
            .map {
                try {
                    map.toMuslimCalendar(it)
                } catch (e: Exception) {
                    Log.e(TAG, "Error mapping calendar data", e)
                    null
                }
            }
            .filterNotNull()

    override fun oneMonth() = db.calendarDao().oneMonth().map { map.toMuslimCalendarList(it) }

    override suspend fun loadQuranArab() {
        val jobInfo = JobInfo.Builder(2, ComponentName(context, QuranJobService::class.java))
            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
            .setOverrideDeadline(0)
            .build()

        val scheduler = context.getSystemService(JobScheduler::class.java)
        scheduler.schedule(jobInfo)
    }

    override fun downloadSurah(suraAyahs: List<SurahList>, url: String) {
        val componentName = ComponentName(context, DownloadJobService::class.java)
        val jobInfo = JobInfo.Builder(1, componentName)
            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
            .setPersisted(true)
            .setExtras(
                PersistableBundle().apply {
                    putString(DownloadJobService.KEY_FILE_URL, url)
                    putString(DownloadJobService.KEY_SURA, gson.toJson(suraAyahs))
                }
            )
            .build()

        val jobScheduler = context.getSystemService(JobScheduler::class.java)
        jobScheduler?.schedule(jobInfo)
    }

    override fun getSurah() = flow {
        db.suraDao().getAllSura().collect {
            emit(map.toSuraList(it))
        }
    }

    override fun getSuraByNumber(number: Int) = flow {
        db.suraDao().getSuraById(number).collect {
            emit(map.toSura(it))
        }
    }

    override fun getSurahById(sura: String) = flow {
        db.surahAyahDao().getSurahAyahsById(sura).collect {
            emit(map.toSuraAyahList(it))
        }
    }

    override fun getSura(number: Int) = flow {
        apiService.getSura(number).result?.let {
            val surah = withContext(Dispatchers.IO) { Surah(map.toSurahList(it)) }
            emit(surah)
        }
    }

    override fun getAudioPath(sura: String) = flow {
        try {
            db.audioPathDao().getAudioPathBySura(sura).collect {
                emit(AudioPath(it.audioPath, it.sura))
            }
        } catch (_: Exception) {
            emit(AudioPath(null, sura))
        }
    }
}

private const val TAG = "CalendarRepositoryImpl"