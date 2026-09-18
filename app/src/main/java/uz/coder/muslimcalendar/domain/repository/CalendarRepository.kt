package uz.coder.muslimcalendar.domain.repository

import kotlinx.coroutines.flow.Flow
import uz.coder.muslimcalendar.domain.model.AudioPath
import uz.coder.muslimcalendar.domain.model.CalendarAvailability
import uz.coder.muslimcalendar.shared.domain.model.MuslimCalendar
import uz.coder.muslimcalendar.models.model.SuraAyah
import uz.coder.muslimcalendar.domain.model.quran.Sura
import uz.coder.muslimcalendar.domain.model.quran.Surah
import uz.coder.muslimcalendar.domain.model.quran.SurahList

interface CalendarRepository {

    // Calendar
    suspend fun loading(
        longitude: Double,
        latitude: Double
    )

    suspend fun getCalendarAvailability(): CalendarAvailability

    suspend fun region(region: String)

    suspend fun remove()

    fun presentDay(): Flow<MuslimCalendar>

    fun oneMonth(): Flow<List<MuslimCalendar>>

    fun getTodayPrayerTimes(): Flow<MuslimCalendar?>

    fun getTomorrowPrayerTimes(): Flow<MuslimCalendar?>

    fun getPrayerTimesForRange(
        start: Long,
        end: Long
    ): Flow<List<MuslimCalendar>>

    // Quran
    suspend fun loadQuranArab(): Result<Result<Int>>

    fun getSurah(): Flow<List<Sura>>

    fun downloadSurah(
        suraAyahs: List<SurahList>,
        url: String
    ): Result<Result<Int>>

    fun getSuraByNumber(number: Int): Flow<Sura>

    fun getSurahById(sura: String): Flow<List<SuraAyah>>

    fun getSura(number: Int): Flow<Surah>

    fun getAudioPath(sura: String): Flow<AudioPath>
}
