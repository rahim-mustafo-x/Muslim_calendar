package uz.coder.muslimcalendar.domain.usecase

import uz.coder.muslimcalendar.domain.repository.CalendarRepository

class PresentDayUseCase(private val repository: CalendarRepository) {
    operator fun invoke() = repository.presentDay()
}