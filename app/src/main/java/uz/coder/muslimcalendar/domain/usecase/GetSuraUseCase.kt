package uz.coder.muslimcalendar.domain.usecase

import uz.coder.muslimcalendar.domain.repository.CalendarRepository
import javax.inject.Inject

class GetSuraUseCase @Inject constructor(private val repository: CalendarRepository) {
    operator fun invoke(number:Int) = repository.getSura(number)
}