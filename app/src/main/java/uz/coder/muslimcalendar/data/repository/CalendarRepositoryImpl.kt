package uz.coder.muslimcalendar.data.repository

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
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import uz.coder.muslimcalendar.R
import uz.coder.muslimcalendar.db.AppDatabase
import uz.coder.muslimcalendar.data.ktor.ApiService.Companion.apiService
import uz.coder.muslimcalendar.data.map.CalendarMap
import uz.coder.muslimcalendar.domain.model.AudioPath
import uz.coder.muslimcalendar.models.model.SuraAyah
import uz.coder.muslimcalendar.domain.model.quran.Sura
import uz.coder.muslimcalendar.models.model.quran.Surah
import uz.coder.muslimcalendar.models.model.quran.SurahList
import uz.coder.muslimcalendar.data.service.DownloadWorker
import uz.coder.muslimcalendar.data.service.PrayerTimeJobService
import uz.coder.muslimcalendar.data.service.QuranWorkManager
import uz.coder.muslimcalendar.domain.repository.CalendarRepository
import uz.coder.muslimcalendar.todo.REGION
import java.time.LocalDate
import java.util.Calendar.DAY_OF_MONTH
import java.util.Calendar.MONTH
import java.util.Calendar.getInstance
import java.util.UUID
import java.util.concurrent.TimeUnit

data class CalendarRepositoryImpl(private val application: Application): CalendarRepository {
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

        WorkManager.getInstance(application)
            .enqueueUniqueWork("QuranWork", ExistingWorkPolicy.KEEP, workRequest)
    }

    override fun getSurah() = flow<List<Sura>> {
        db.suraDao().getAllSura().collect{
            emit(
                map.toSuraList(it)
            )
        }
    }

    override fun downloadSurah(
        suraAyahs: List<SurahList>,
        url: String
    ): Flow<UUID> = flow {
        // --- WorkRequest tayyorlash ---
        val inputData = Data.Builder()
            .putString(DownloadWorker.KEY_FILE_URL, url)
            .putString(DownloadWorker.KEY_SURA, suraAyahs.first().sura)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<DownloadWorker>()
            .setInputData(inputData)
            .build()

        val workManager = WorkManager.getInstance(application)

        // Ishni navbatga qo‘yish
        workManager.enqueue(workRequest)

        // --- Worker kerakli holatga yetishini kutamiz ---
        workManager
            .getWorkInfoByIdFlow(workRequest.id)          // Flow<WorkInfo>
            .filter { info ->                             // faqat RUNNING yoki ENQUEUED
                info?.state == WorkInfo.State.RUNNING ||
                        info?.state == WorkInfo.State.ENQUEUED
            }
            .first()                                      // birinchi marta shu holat bo‘lsa — kutib turadi
        // (suspend bo‘ladi)
        // <-- shu yerda kutish tugadi

        // --- Maʼlumotlar bazasiga yozib qo‘yish ---
        db.surahAyahDao().insertAll(
            map.toSuraAyahDbModels(suraAyahs)
        )

        // --- Natijani emit qilamiz ---
        emit(workRequest.id)
    }.flowOn(Dispatchers.IO)

    fun observeProgress(id: UUID): Flow<Int> {
        return WorkManager.getInstance(application)
            .getWorkInfoByIdFlow(id)
            .map { it?.progress?.getInt(DownloadWorker.KEY_PROGRESS, 0)?:0 }
    }


    override fun getSuraByNumber(number: Int) = flow<Sura> {
        db.suraDao().getSuraById(number).collect {
            emit(map.toSura(it))
        }
    }

    override fun getSurahById(sura: String) = flow<List<SuraAyah>> {
        db.surahAyahDao().getSurahAyahsById(sura).collect {
            emit(map.toSuraAyahList(it))
        }
    }

    override fun getSura(number: Int) = flow<Surah> {
        apiService.getSura(number).result?.let {
            val surah = withContext(Dispatchers.IO) { Surah(map.toSurahList(it)) }
            emit(surah)
        }
    }
    override fun getAudioPath(sura: String) = flow<AudioPath> {
        try{
            Log.d(TAG, "getAudioPath: bo'sh emas")
            db.audioPathDao().getAudioPathBySura(sura).collect{
                emit(AudioPath(it.audioPath, it.sura))
            }
        }catch(_ :Exception){
            Log.d(TAG, "getAudioPath: bo'sh")
            emit(AudioPath(null, sura))
        }
    }
}

private const val TAG = "CalendarRepositoryImpl"