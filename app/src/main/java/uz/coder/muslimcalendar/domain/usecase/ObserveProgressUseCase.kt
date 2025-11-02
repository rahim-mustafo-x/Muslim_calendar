package uz.coder.muslimcalendar.domain.usecase

import uz.coder.muslimcalendar.domain.repository.CalendarRepository
import java.util.UUID
import javax.inject.Inject

class ObserveProgressUseCase @Inject constructor(private val repository: CalendarRepository) {
    operator fun invoke(id: UUID) = repository.observeProgress(id)
}
