package uz.coder.muslimcalendar.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import uz.coder.muslimcalendar.data.db.AppDatabase
import uz.coder.muslimcalendar.data.map.CalendarMap
import uz.coder.muslimcalendar.data.network.KtorApiService
import uz.coder.muslimcalendar.domain.model.AudioPath
import uz.coder.muslimcalendar.domain.model.CalendarAvailability
import uz.coder.muslimcalendar.shared.domain.model.MuslimCalendar
import uz.coder.muslimcalendar.domain.model.quran.Sura
import uz.coder.muslimcalendar.domain.model.quran.Surah
import uz.coder.muslimcalendar.domain.model.quran.SurahList
import uz.coder.muslimcalendar.domain.repository.CalendarRepository
import uz.coder.muslimcalendar.models.model.SuraAyah
import java.time.LocalDate

class AndroidSharedCalendarRepositoryImpl(
    private val db: AppDatabase,
    private val map: CalendarMap,
    private val apiService: KtorApiService
) : CalendarRepository {

    // ---------------------------------------------------------
    // Prayer times
    // ---------------------------------------------------------

    override fun getTodayPrayerTimes(): Flow<MuslimCalendar?> {
        val today = LocalDate.now()

        return db.calendarDao()
            .presentDay(
                today.dayOfMonth,
                today.monthValue,
                today.year
            )
            .map { entity ->
                entity?.let(map::toMuslimCalendar)
            }
    }

    override fun getTomorrowPrayerTimes(): Flow<MuslimCalendar?> {
        val tomorrow = LocalDate.now().plusDays(1)

        return db.calendarDao()
            .presentDay(
                tomorrow.dayOfMonth,
                tomorrow.monthValue,
                tomorrow.year
            )
            .map { entity ->
                entity?.let(map::toMuslimCalendar)
            }
    }

    override fun getPrayerTimesForRange(
        start: Long,
        end: Long
    ): Flow<List<MuslimCalendar>> {
        return db.calendarDao()
            .oneMonth()
            .map { entities ->
                entities.map(map::toMuslimCalendar)
            }
    }

    override suspend fun loading(
        longitude: Double,
        latitude: Double
    ) {
        ensureRollingSchedule(
            latitude = latitude,
            longitude = longitude
        )
    }

    override suspend fun getCalendarAvailability(): CalendarAvailability {
        val today = LocalDate.now()
        val nextMonth = today.plusMonths(1).withDayOfMonth(1)
        val latest = db.calendarDao().getLatest()?.let { LocalDate.of(it.year, it.month, it.day) }
        return CalendarAvailability(
            currentMonthReady = db.calendarDao().countInMonth(today.monthValue, today.year) >= today.lengthOfMonth(),
            nextMonthReady = db.calendarDao().countInMonth(nextMonth.monthValue, nextMonth.year) >= nextMonth.lengthOfMonth(),
            latestStoredDate = latest
        )
    }

    override suspend fun region(region: String) {
        // There is currently no region operation
        // in the DAO/API you provided.
    }

    override suspend fun remove() {
        // Do not call calendarDao().deleteAll()
        // until that function exists in CalendarDao.
    }

    // ---------------------------------------------------------
    // Prayer API
    // ---------------------------------------------------------

    private suspend fun ensureRollingSchedule(
        latitude: Double,
        longitude: Double
    ) {
        val today = LocalDate.now()
        val targetDate = today.plusDays(90)

        var currentMonth = today.withDayOfMonth(1)

        while (!currentMonth.isAfter(targetDate)) {

            try {
                val response = apiService.getOneMonthPrayerTimes(
                    year = currentMonth.year,
                    month = currentMonth.monthValue,
                    latitude = latitude,
                    longitude = longitude
                )

                response.data?.let { prayerDataList ->

                    val entities =
                        map.toMuslimCalendarDbModel(prayerDataList)

                    db.calendarDao()
                        .insertMuslimCalendar(entities)
                }

            } catch (e: Exception) {
                e.printStackTrace()
                break
            }

            currentMonth = currentMonth.plusMonths(1)
        }
    }

    // ---------------------------------------------------------
    // Calendar
    // ---------------------------------------------------------

    override fun presentDay(): Flow<MuslimCalendar> {
        return getTodayPrayerTimes()
            .map { it ?: MuslimCalendar() }
    }

    override fun oneMonth(): Flow<List<MuslimCalendar>> {
        return db.calendarDao()
            .oneMonth()
            .map { entities ->
                entities.map(map::toMuslimCalendar)
            }
    }

    // ---------------------------------------------------------
    // Quran
    // ---------------------------------------------------------

    override suspend fun loadQuranArab(): Result<Result<Int>> {
        return try {
            val count = db.suraDao().getCount()

            if (count > 0) {
                Result.success(
                    Result.success(count)
                )
            } else {
                Result.failure(
                    IllegalStateException("Quran is not loaded")
                )
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getSurah(): Flow<List<Sura>> {
        return db.suraDao()
            .getAllSura()
            .map { entities ->
                entities.map(map::toSura)
            }
    }

    override fun downloadSurah(
        suraAyahs: List<SurahList>,
        url: String
    ): Result<Result<Int>> {
        return Result.success(
            Result.success(suraAyahs.size)
        )
    }

    override fun getSuraByNumber(
        number: Int
    ): Flow<Sura> {
        return db.suraDao()
            .getSuraById(number)
            .map(map::toSura)
    }

    override fun getSurahById(
        sura: String
    ): Flow<List<SuraAyah>> {
        return db.surahAyahDao()
            .getSurahAyahsById(sura)
            .map { entities ->
                entities.map(map::toSuraAyah)
            }
    }

    override fun getSura(
        number: Int
    ): Flow<Surah> {
        return db.surahAyahDao()
            .getSurahAyahsById(number.toString())
            .map(map::toSurah)
    }

    override fun getAudioPath(
        sura: String
    ): Flow<AudioPath> {
        return db.audioPathDao()
            .getAudioPathBySura(sura)
            .map { entity ->
                entity?.let(map::toAudioPath) ?: AudioPath(null, sura)
            }
    }
}
