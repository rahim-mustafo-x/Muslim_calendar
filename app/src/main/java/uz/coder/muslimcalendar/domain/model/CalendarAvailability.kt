package uz.coder.muslimcalendar.domain.model

import java.time.LocalDate

data class CalendarAvailability(
    val currentMonthReady: Boolean,
    val nextMonthReady: Boolean,
    val latestStoredDate: LocalDate?
) {
    val needsDownload: Boolean
        get() = !currentMonthReady || !nextMonthReady
}
