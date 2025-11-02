package uz.coder.muslimcalendar.domain.usecase

import uz.coder.muslimcalendar.domain.repository.CalendarRepository
import javax.inject.Inject

class RemoveUseCase @Inject constructor(private val repository: CalendarRepository) {
    suspend operator fun invoke() = repository.remove()
}