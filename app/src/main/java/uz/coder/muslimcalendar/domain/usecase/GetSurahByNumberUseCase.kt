package uz.coder.muslimcalendar.domain.usecase

import uz.coder.muslimcalendar.domain.repository.CalendarRepository

class GetSurahByNumberUseCase(private val repository: CalendarRepository) {
    operator fun invoke(number:Int) = repository.getSuraByNumber(number)
}