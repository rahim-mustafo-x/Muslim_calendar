package uz.coder.muslimcalendar.data.repository

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.os.PersistableBundle
import android.util.Log
import androidx.work.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import uz.coder.muslimcalendar.SharedPref
import uz.coder.muslimcalendar.data.db.AppDatabase
import uz.coder.muslimcalendar.data.map.CalendarMap
import uz.coder.muslimcalendar.data.network.ApiServiceQuranUzbek
import uz.coder.muslimcalendar.data.service.DownloadWorker
import uz.coder.muslimcalendar.data.service.PrayerTimeJobService
import uz.coder.muslimcalendar.data.service.QuranWorkManager
import uz.coder.muslimcalendar.domain.model.AudioPath
import uz.coder.muslimcalendar.domain.repository.CalendarRepository
import uz.coder.muslimcalendar.models.model.quran.Surah
import uz.coder.muslimcalendar.models.model.quran.SurahList
import uz.coder.muslimcalendar.todo.REGION
import java.time.LocalDate
import java.util.Calendar.DAY_OF_MONTH
import java.util.Calendar.MONTH
import java.util.Calendar.getInstance
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
data class CalendarRepositoryImpl @Inject constructor(
    private val preferences: SharedPref,
    private val db: AppDatabase,
    private val map: CalendarMap,
    @ApplicationContext private val context: Context,
    private val apiService: ApiServiceQuranUzbek
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
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<QuranWorkManager>()
            .setConstraints(constraints)
            .setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork("QuranWork", ExistingWorkPolicy.KEEP, workRequest)
    }

    override fun downloadSurah(suraAyahs: List<SurahList>, url: String): Flow<UUID> = flow {
        val inputData = Data.Builder()
            .putString(DownloadWorker.KEY_FILE_URL, url)
            .putString(DownloadWorker.KEY_SURA, suraAyahs.first().sura)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<DownloadWorker>()
            .setInputData(inputData)
            .build()

        val workManager = WorkManager.getInstance(context)
        workManager.enqueue(workRequest)

        emit(workRequest.id)

        withContext(Dispatchers.IO) {
            db.surahAyahDao().insertAll(map.toSuraAyahDbModels(suraAyahs))
        }
    }.flowOn(Dispatchers.IO)

    override fun observeProgress(id: UUID): Flow<Int> =
        WorkManager.getInstance(context)
            .getWorkInfoByIdFlow(id)
            .map { it?.progress?.getInt(DownloadWorker.KEY_PROGRESS, 0) ?: 0 }
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