package uz.coder.muslimcalendar.domain.usecase

import uz.coder.muslimcalendar.domain.repository.CalendarRepository

class GetSurahUseCase(private val repository: CalendarRepository) {
    operator fun invoke() = repository.getSurah()
}