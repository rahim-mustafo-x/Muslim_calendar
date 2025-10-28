package uz.coder.muslimcalendar.domain.usecase

import uz.coder.muslimcalendar.domain.repository.CalendarRepository

class RemoveUseCase(private val repository: CalendarRepository) {
    suspend operator fun invoke() = repository.remove()
}