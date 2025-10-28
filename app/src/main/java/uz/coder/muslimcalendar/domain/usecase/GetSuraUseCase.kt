package uz.coder.muslimcalendar.domain.usecase

import uz.coder.muslimcalendar.domain.repository.CalendarRepository

class GetSuraUseCase(private val repository: CalendarRepository) {
    operator fun invoke(number:Int) = repository.getSura(number)
}