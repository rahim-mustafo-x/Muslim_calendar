package uz.coder.muslimcalendar.shared.domain

import kotlinx.datetime.*
import uz.coder.muslimcalendar.shared.domain.model.MuslimCalendar

class PrayerEngine {
    
    fun getNextPrayer(currentTime: LocalDateTime, today: MuslimCalendar, tomorrow: MuslimCalendar): Pair<String, LocalDateTime>? {
        val todayPrayers = getPrayerTimesWithDateTime(today, currentTime.date)
        
        // Find first prayer today that is after now
        val nextToday = todayPrayers.find { it.second > currentTime }
        if (nextToday != null) return nextToday
        
        // If no more prayers today, take first prayer tomorrow (Fajr)
        val tomorrowPrayers = getPrayerTimesWithDateTime(tomorrow, currentTime.date.plus(1, DateTimeUnit.DAY))
        return tomorrowPrayers.firstOrNull()
    }

    private fun getPrayerTimesWithDateTime(calendar: MuslimCalendar, date: LocalDate): List<Pair<String, LocalDateTime>> {
        val names = listOf("Bomdod", "Quyosh", "Peshin", "Asr", "Shom", "Xufton")
        return calendar.items.mapIndexed { index, timeStr ->
            try {
                if (timeStr.isEmpty() || !timeStr.contains(":")) return@mapIndexed null
                val parts = timeStr.trim().split(":")
                if (parts.size < 2) return@mapIndexed null
                val h = parts[0].toIntOrNull() ?: return@mapIndexed null
                val m = parts[1].toIntOrNull() ?: return@mapIndexed null
                names[index] to LocalDateTime(date.year, date.monthNumber, date.dayOfMonth, h, m)
            } catch (e: Exception) {
                null
            }
        }.filterNotNull()
    }
    
    fun calculateCountdown(now: LocalDateTime, nextPrayerTime: LocalDateTime): String {
        val nowInstant = now.toInstant(TimeZone.currentSystemDefault())
        val nextInstant = nextPrayerTime.toInstant(TimeZone.currentSystemDefault())
        val duration = nextInstant - nowInstant
        
        val hours = duration.inWholeHours
        val minutes = duration.inWholeMinutes % 60
        val seconds = duration.inWholeSeconds % 60
        
        return if (hours > 0) {
            "${hours}s ${minutes}m"
        } else {
            "${minutes}m ${seconds}s"
        }
    }
}
