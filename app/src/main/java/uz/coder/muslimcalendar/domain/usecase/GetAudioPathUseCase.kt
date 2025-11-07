package uz.coder.muslimcalendar.domain.usecase

import uz.coder.muslimcalendar.domain.repository.CalendarRepository
import javax.inject.Inject

class GetAudioPathUseCase @Inject constructor(private val repository: CalendarRepository) {
    operator fun invoke(sura: String) = repository.getAudioPath(sura)
}