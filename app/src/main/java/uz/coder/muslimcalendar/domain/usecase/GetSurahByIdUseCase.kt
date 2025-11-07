package uz.coder.muslimcalendar.domain.usecase

import uz.coder.muslimcalendar.domain.repository.CalendarRepository
import javax.inject.Inject

class GetSurahByIdUseCase @Inject constructor(private val repository: CalendarRepository) {
    operator fun invoke(sura: String) = repository.getSurahById(sura)
}