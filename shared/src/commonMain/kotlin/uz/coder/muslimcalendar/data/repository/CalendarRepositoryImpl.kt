package uz.coder.muslimcalendar.shared.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.*
import uz.coder.muslimcalendar.shared.data.network.KtorApiService
import uz.coder.muslimcalendar.domain.model.MuslimCalendar
import uz.coder.muslimcalendar.shared.domain.repository.CalendarRepository

class CalendarRepositoryImpl(
    private val apiService: KtorApiService
) : CalendarRepository {

    override fun getTodayPrayerTimes(): Flow<MuslimCalendar?> = flow {
        // Implementation will involve local DB
        emit(null)
    }

    override fun getTomorrowPrayerTimes(): Flow<MuslimCalendar?> = flow {
        emit(null)
    }

    override fun getPrayerTimesForRange(start: Long, end: Long): Flow<List<MuslimCalendar>> = flow {
        emit(emptyList())
    }

    override suspend fun refreshPrayerTimes(latitude: Double, longitude: Double) {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        apiService.getOneMonthPrayerTimes(now.year, now.monthNumber, latitude, longitude)
        // Save to DB...
    }

    override suspend fun ensureRollingSchedule(latitude: Double, longitude: Double) {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val ninetyDaysLater = now.plus(90, DateTimeUnit.DAY)
        
        var currentMonth = now
        while (currentMonth <= ninetyDaysLater) {
            // Check if month exists in DB, if not fetch and save
            // For now just calling API as placeholder
            apiService.getOneMonthPrayerTimes(currentMonth.year, currentMonth.monthNumber, latitude, longitude)
            currentMonth = currentMonth.plus(1, DateTimeUnit.MONTH)
        }
    }
}
