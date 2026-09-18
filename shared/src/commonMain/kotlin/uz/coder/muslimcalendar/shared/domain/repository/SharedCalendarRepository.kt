package uz.coder.muslimcalendar.shared.domain.repository

import kotlinx.coroutines.flow.Flow
import uz.coder.muslimcalendar.shared.domain.model.MuslimCalendar

interface SharedCalendarRepository {
    fun getTodayPrayerTimes(): Flow<MuslimCalendar?>
    fun getTomorrowPrayerTimes(): Flow<MuslimCalendar?>
    fun getPrayerTimesForRange(start: Long, end: Long): Flow<List<MuslimCalendar>>
    suspend fun refreshPrayerTimes(latitude: Double, longitude: Double)
    suspend fun ensureRollingSchedule(latitude: Double, longitude: Double)
}
