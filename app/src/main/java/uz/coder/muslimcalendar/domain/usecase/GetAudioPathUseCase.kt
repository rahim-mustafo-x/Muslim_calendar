package uz.coder.muslimcalendar.domain.usecase

import uz.coder.muslimcalendar.domain.repository.CalendarRepository

class GetAudioPathUseCase(private val repository: CalendarRepository) {
    operator fun invoke(sura: String) = repository.getAudioPath(sura)
}