package uz.coder.muslimcalendar.data.repository

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.os.PersistableBundle
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.withContext
import uz.coder.muslimcalendar.SharedPref
import uz.coder.muslimcalendar.data.db.AppDatabase
import uz.coder.muslimcalendar.data.map.CalendarMap
import uz.coder.muslimcalendar.data.network.ApiServiceQuranUzbek
import uz.coder.muslimcalendar.data.service.DownloadJobService
import uz.coder.muslimcalendar.data.service.JobIds
import uz.coder.muslimcalendar.data.service.PrayerTimeJobService
import uz.coder.muslimcalendar.data.service.QuranJobService
import uz.coder.muslimcalendar.domain.model.AudioPath
import uz.coder.muslimcalendar.domain.model.MuslimCalendar
import uz.coder.muslimcalendar.domain.model.quran.Sura
import uz.coder.muslimcalendar.domain.model.quran.Surah
import uz.coder.muslimcalendar.domain.model.quran.SurahList
import uz.coder.muslimcalendar.domain.repository.CalendarRepository
import uz.coder.muslimcalendar.models.model.SuraAyah
import uz.coder.muslimcalendar.todo.REGION
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CalendarRepositoryImpl @Inject constructor(
    private val preferences: SharedPref,
    private val db: AppDatabase,
    private val map: CalendarMap,
    @ApplicationContext private val context: Context,
    private val apiService: ApiServiceQuranUzbek,
    private val gson: Gson
) : CalendarRepository {

    private val calendarCache = mutableMapOf<String, MuslimCalendar?>()
    private val surahCache = mutableMapOf<Int, Sura?>()
    private var cacheTimestamp = 0L
    companion object{
        private const val CACHE_DURATION_MS = 5 * 60 * 1000L
    }

    override suspend fun region(region: String) {
        preferences.saveValue(REGION, region)
        clearCache()
    }

    override suspend fun remove() {
        val today = LocalDate.now()
        runCatching {
            db.calendarDao().deleteOldData(today.dayOfMonth, today.monthValue)
        }.onFailure {
            runCatching { db.calendarDao().deleteExceptLast30Days() }
        }
    }

    override fun presentDay(): Flow<MuslimCalendar> = flow {
        val key = "presentDay_${LocalDate.now()}"
        if (isCacheValid() && calendarCache.containsKey(key)) {
            calendarCache[key]?.let { emit(it); return@flow }
        }

        db.calendarDao().presentDay(LocalDate.now().dayOfMonth, LocalDate.now().monthValue)
            .mapNotNull(map::toMuslimCalendar)
            .collect {
                calendarCache[key] = it
                cacheTimestamp = System.currentTimeMillis()
                emit(it)
            }
    }.catch { calendarCache["presentDay_${LocalDate.now()}"]?.let { emit(it) } }
        .flowOn(Dispatchers.IO)

    override fun oneMonth(): Flow<List<MuslimCalendar>> = flow {
        db.calendarDao().oneMonth()
            .map { it.map(map::toMuslimCalendar) }
            .collect { emit(it) }
    }.flowOn(Dispatchers.IO)

    override suspend fun loadQuranArab(): Result<Result<Int>> {
        val success = Result.success(
            runCatching {
                val job = JobInfo.Builder(
                    JobIds.QURAN_LOAD,
                    ComponentName(context, QuranJobService::class.java)
                )
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setMinimumLatency(0)
                    .setOverrideDeadline(0)
                    .build()
                    .also { context.getSystemService(JobScheduler::class.java)?.schedule(it) }
                job.hashCode() // yoki schedule() ning int natijasi
            }
        )
        return success
    }

    override fun getSurah(): Flow<List<Sura>> = flow {
        if (isCacheValid() && surahCache.isNotEmpty()) {
            emit(surahCache.values.filterNotNull())
            return@flow
        }

        db.suraDao().getAllSura()
            .map { list ->
                list.map(map::toSura).also {
                    surahCache.clear()
                    it.forEach { sura -> surahCache[sura.number] = sura }
                    cacheTimestamp = System.currentTimeMillis()
                }
            }
            .collect { emit(it) }
    }.flowOn(Dispatchers.IO)

    override fun getSuraByNumber(number: Int): Flow<Sura> = flow {
        surahCache[number]?.let { emit(it); return@flow }

        db.suraDao().getSuraById(number)
            .map(map::toSura)
            .collect {
                surahCache[number] = it
                cacheTimestamp = System.currentTimeMillis()
                emit(it)
            }
    }.flowOn(Dispatchers.IO)

    override fun getSurahById(sura: String): Flow<List<SuraAyah>> = flow {
        db.surahAyahDao().getSurahAyahsById(sura)
            .map { it.map(map::toSuraAyah) }
            .collect { emit(it) }
    }.flowOn(Dispatchers.IO)

    override fun getSura(number: Int): Flow<Surah> = flow {
        var retry = 0
        while (true) {
            try {
                val result = apiService.getSura(number).result ?: throw Exception("Empty response")
                emit(Surah(withContext(Dispatchers.IO) { map.toSurahList(result) }))
                return@flow
            } catch (e: Exception) {
                retry++
                delay(1000L * retry)
                if (retry >= 3) throw e
            }
        }
    }.flowOn(Dispatchers.IO)

    override fun getAudioPath(sura: String): Flow<AudioPath> = flow {
        runCatching {
            db.audioPathDao().getAudioPathBySura(sura)
                .map { AudioPath(it.audioPath, it.sura) }
                .collect { emit(it) }
        }.onFailure { emit(AudioPath(null, sura)) }
    }.flowOn(Dispatchers.IO)

    override fun downloadSurah(suraAyahs: List<SurahList>, url: String): Result<Result<Int>> {
        val success = Result.success(
            runCatching {
                val bundle = PersistableBundle().apply {
                    putString(DownloadJobService.KEY_FILE_URL, url)
                    putString(DownloadJobService.KEY_SURA, gson.toJson(suraAyahs))
                }

                val job = JobInfo.Builder(
                    JobIds.AUDIO_DOWNLOAD,
                    ComponentName(context, DownloadJobService::class.java)
                )
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setPersisted(true)
                    .setMinimumLatency(0)
                    .setOverrideDeadline(0)
                    .setExtras(bundle)
                    .build()
                    .also { context.getSystemService(JobScheduler::class.java)?.schedule(it) }
                job.hashCode()
            }
        )
        return success
    }

    override suspend fun loading(longitude: Double, latitude: Double) {
        if (longitude != 0.0 && latitude != 0.0) {
            val extras = PersistableBundle().apply {
                putDouble(PrayerTimeJobService.LATITUDE, latitude)
                putDouble(PrayerTimeJobService.LONGITUDE, longitude)
            }
            JobInfo.Builder(
                JobIds.PRAYER_TIME,
                ComponentName(context, PrayerTimeJobService::class.java)
            )
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setMinimumLatency(0)
                .setOverrideDeadline(0)
                .setExtras(extras)
                .build()
                .also { context.getSystemService(JobScheduler::class.java)?.schedule(it) }
        }
    }

    private fun isCacheValid() = (System.currentTimeMillis() - cacheTimestamp) < CACHE_DURATION_MS
    private fun clearCache() {
        calendarCache.clear()
        surahCache.clear()
        cacheTimestamp = 0L
    }
}