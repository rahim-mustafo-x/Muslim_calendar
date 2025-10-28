package uz.coder.muslimcalendar.domain.usecase

import uz.coder.muslimcalendar.domain.repository.CalendarRepository

class OneMonthDayUseCase(private val repository: CalendarRepository) {
    operator fun invoke() = repository.oneMonth()
}