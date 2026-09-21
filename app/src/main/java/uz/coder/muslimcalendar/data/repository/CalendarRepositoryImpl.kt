package uz.coder.muslimcalendar.data.repository

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.util.Log
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import androidx.work.workDataOf
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
import uz.coder.muslimcalendar.data.network.KtorApiService
import uz.coder.muslimcalendar.data.service.DownloadWorker
import uz.coder.muslimcalendar.data.service.JobIds
import uz.coder.muslimcalendar.data.service.QuranJobService
import uz.coder.muslimcalendar.domain.model.AudioPath
import uz.coder.muslimcalendar.domain.model.CalendarAvailability
import uz.coder.muslimcalendar.domain.model.SuraAyah
import uz.coder.muslimcalendar.domain.model.quran.Sura
import uz.coder.muslimcalendar.domain.model.quran.Surah
import uz.coder.muslimcalendar.domain.model.quran.SurahList
import uz.coder.muslimcalendar.domain.repository.CalendarRepository
import uz.coder.muslimcalendar.shared.domain.model.MuslimCalendar
import uz.coder.muslimcalendar.todo.REGION
import uz.coder.muslimcalendar.todo.hasInternetConnection
import java.time.LocalDate
import kotlin.time.Duration.Companion.milliseconds

class CalendarRepositoryImpl(
    private val preferences: SharedPref,
    private val db: AppDatabase,
    private val map: CalendarMap,
    private val context: Context,
    private val ktorApiService: KtorApiService
) : CalendarRepository {

    private val calendarCache = mutableMapOf<String, MuslimCalendar?>()
    private val surahCache = mutableMapOf<Int, Sura?>()
    private var cacheTimestamp = 0L

    companion object {
        private const val TAG = "CalendarRepository"
        private const val CACHE_DURATION_MS = 5 * 60 * 1000L
    }

    // ============================================================
    // CALENDAR LOADING
    // ============================================================

    override suspend fun loading(longitude: Double, latitude: Double) {
        if (!isValidCoordinates(latitude, longitude)) {
            Log.w(TAG, "Invalid coordinates: lat=$latitude, lon=$longitude")
            return
        }

        try {
            if (!context.hasInternetConnection()) {
                Log.d(TAG, "Calendar sync skipped: no validated internet connection")
                return
            }
            val today = LocalDate.now()
            val nextMonth = today.plusMonths(1).withDayOfMonth(1)
            val availability = getCalendarAvailability()

            if (!availability.currentMonthReady) {
                downloadAndSaveMonth(
                    year = today.year,
                    month = today.monthValue,
                    latitude = latitude,
                    longitude = longitude
                )
            }

            if (!availability.nextMonthReady) {
                downloadAndSaveMonth(
                    year = nextMonth.year,
                    month = nextMonth.monthValue,
                    latitude = latitude,
                    longitude = longitude
                )
            }

            clearCache()
            Log.d(TAG, "Calendar loading completed for current and next month")

        } catch (e: Exception) {
            Log.e(TAG, "Failed to load prayer calendar", e)
            throw e
        }
    }

    override suspend fun getCalendarAvailability(): CalendarAvailability {
        val today = LocalDate.now()
        val nextMonth = today.plusMonths(1).withDayOfMonth(1)
        val currentCount = db.calendarDao().countInMonth(today.monthValue, today.year)
        val nextCount = db.calendarDao().countInMonth(nextMonth.monthValue, nextMonth.year)
        val latest = db.calendarDao().getLatest()?.let {
            LocalDate.of(it.year, it.month, it.day)
        }
        return CalendarAvailability(
            currentMonthReady = currentCount >= today.lengthOfMonth(),
            nextMonthReady = nextCount >= nextMonth.lengthOfMonth(),
            latestStoredDate = latest
        )
    }

    private fun isValidCoordinates(latitude: Double, longitude: Double): Boolean {
        return latitude in -90.0..90.0 &&
                longitude in -180.0..180.0 &&
                !(latitude == 0.0 && longitude == 0.0)
    }

    private suspend fun downloadAndSaveMonth(
        year: Int,
        month: Int,
        latitude: Double,
        longitude: Double
    ) {
        Log.d(TAG, "Downloading prayer calendar: $year-$month")

        val result = ktorApiService.getOneMonthPrayerTimes(
            year = year,
            month = month,
            latitude = latitude,
            longitude = longitude
        )

        val prayerDataList = result.data

        if (prayerDataList.isNullOrEmpty()) {
            Log.w(TAG, "API returned no prayer data for $year-$month")
            return
        }

        val dbModels = map.toMuslimCalendarDbModel(prayerDataList)

        if (dbModels.isEmpty()) {
            Log.w(TAG, "Mapper produced no database models for $year-$month")
            return
        }

        db.calendarDao().insertMuslimCalendar(dbModels)
        Log.d(TAG, "Saved ${dbModels.size} prayer days for $year-$month")
    }

    // ============================================================
    // REGION & HOME
    // ============================================================

    override suspend fun region(region: String) {
        preferences.saveValue(REGION, region)
        clearCache()
    }

    override fun getTodayPrayerTimes(): Flow<MuslimCalendar?> {
        val today = LocalDate.now()
        return db.calendarDao()
            .presentDay(
                day = today.dayOfMonth,
                month = today.monthValue,
                year = today.year
            )
            .map { dbModel -> dbModel?.let(map::toMuslimCalendar) }
            .flowOn(Dispatchers.IO)
    }

    override fun getTomorrowPrayerTimes(): Flow<MuslimCalendar?> {
        val tomorrow = LocalDate.now().plusDays(1)
        return db.calendarDao()
            .presentDay(
                day = tomorrow.dayOfMonth,
                month = tomorrow.monthValue,
                year = tomorrow.year
            )
            .map { dbModel -> dbModel?.let(map::toMuslimCalendar) }
            .flowOn(Dispatchers.IO)
    }

    override fun getPrayerTimesForRange(start: Long, end: Long): Flow<List<MuslimCalendar>> {
        return db.calendarDao()
            .oneMonth()
            .map { list -> list.map(map::toMuslimCalendar) }
            .flowOn(Dispatchers.IO)
    }

    override suspend fun remove() {
        try {
            db.calendarDao().deleteCalendar()
            clearCache()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to remove calendar data", e)
            throw e
        }
    }

    override fun presentDay(): Flow<MuslimCalendar> = flow {
        val today = LocalDate.now()
        val key = "presentDay_$today"

        if (isCacheValid() && calendarCache.containsKey(key)) {
            calendarCache[key]?.let {
                emit(it)
                return@flow
            }
        }

        db.calendarDao()
            .presentDay(
                day = today.dayOfMonth,
                month = today.monthValue,
                year = today.year
            )
            .mapNotNull { map.toMuslimCalendar(it) }
            .collect { calendar ->
                calendarCache[key] = calendar
                cacheTimestamp = System.currentTimeMillis()
                emit(calendar)
            }
    }.catch { error ->
        Log.e(TAG, "Failed to get present day", error)
        calendarCache["presentDay_${LocalDate.now()}"]?.let { emit(it) }
    }.flowOn(Dispatchers.IO)

    override fun oneMonth(): Flow<List<MuslimCalendar>> {
        return db.calendarDao()
            .oneMonth()
            .map { list -> list.map(map::toMuslimCalendar) }
            .flowOn(Dispatchers.IO)
    }

    // ============================================================
    // QURAN & AUDIO
    // ============================================================

    override suspend fun loadQuranArab(): Result<Int> = runCatching {
        val job = JobInfo.Builder(
            JobIds.QURAN_LOAD,
            ComponentName(context, QuranJobService::class.java)
        )
            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
            .setMinimumLatency(0)
            .setOverrideDeadline(0)
            .build()

        (context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as? JobScheduler)?.schedule(job)
        job.id
    }

    override fun getSurah(): Flow<List<Sura>> = flow {
        if (isCacheValid() && surahCache.isNotEmpty()) {
            emit(surahCache.values.filterNotNull())
            return@flow
        }

        db.suraDao()
            .getAllSura()
            .map { list ->
                list.map(map::toSura).also { suras ->
                    surahCache.clear()
                    suras.forEach { sura -> surahCache[sura.number] = sura }
                    cacheTimestamp = System.currentTimeMillis()
                }
            }
            .collect { emit(it) }
    }.flowOn(Dispatchers.IO)

    override fun getSuraByNumber(number: Int): Flow<Sura> = flow {
        surahCache[number]?.let {
            emit(it)
            return@flow
        }

        db.suraDao()
            .getSuraById(number)
            .map(map::toSura)
            .collect {
                surahCache[number] = it
                cacheTimestamp = System.currentTimeMillis()
                emit(it)
            }
    }.flowOn(Dispatchers.IO)

    override fun getSurahById(sura: String): Flow<List<SuraAyah>> = flow {
        db.surahAyahDao()
            .getSurahAyahsById(sura)
            .map { list -> list.map(map::toSuraAyah) }
            .collect { emit(it) }
    }.flowOn(Dispatchers.IO)

    override fun getSura(number: Int): Flow<Surah> = flow {
        var retry = 0
        while (true) {
            try {
                val result = ktorApiService.getSura(number).result
                    ?: throw Exception("Empty response")

                val surahList = withContext(Dispatchers.IO) {
                    map.toSurahList(result)
                }

                emit(Surah(surahList))
                return@flow
            } catch (e: Exception) {
                retry++
                if (retry >= 3) throw e
                delay((1000L * retry).milliseconds)
            }
        }
    }.flowOn(Dispatchers.IO)

    override fun getAudioPath(sura: String): Flow<AudioPath> = flow {
        runCatching {
            db.audioPathDao()
                .getAudioPathBySura(sura)
                .map { AudioPath(it?.audioPath, it?.sura ?: sura) }
                .collect { emit(it) }
        }.onFailure {
            emit(AudioPath(null, sura))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun downloadSurah(
        suraAyahs: List<SurahList>,
        url: String
    ): Result<Int> = runCatching {
        val suraNumber = suraAyahs.firstOrNull()?.sura ?: "0"

        // Save text data to DB first to avoid passing large payload to WorkManager
        db.surahAyahDao().insertAll(map.toSuraAyahDbModels(suraAyahs))

        val workRequest = OneTimeWorkRequestBuilder<DownloadWorker>()
            .setInputData(
                workDataOf(
                    DownloadWorker.KEY_FILE_URL to url,
                    DownloadWorker.KEY_SURA to suraNumber
                )
            )
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .build()

        // Prevent duplicate download workers running concurrently for the same Surah
        WorkManager.getInstance(context).enqueueUniqueWork(
            "download_surah_$suraNumber",
            ExistingWorkPolicy.KEEP,
            workRequest
        )
        suraAyahs.size
    }

    // ============================================================
    // CACHE
    // ============================================================

    private fun isCacheValid(): Boolean {
        return System.currentTimeMillis() - cacheTimestamp < CACHE_DURATION_MS
    }

    private fun clearCache() {
        calendarCache.clear()
        surahCache.clear()
        cacheTimestamp = 0L
    }
}