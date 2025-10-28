package uz.coder.muslimcalendar.domain.usecase

import uz.coder.muslimcalendar.domain.repository.CalendarRepository

class GetSurahByIdUseCase(private val repository: CalendarRepository) {
    operator fun invoke(sura: String) = repository.getSurahById(sura)
}