package uz.coder.muslimcalendar.domain.usecase

import uz.coder.muslimcalendar.domain.repository.CalendarRepository
import javax.inject.Inject

class LoadingUseCase @Inject constructor(private val repository: CalendarRepository) {
    suspend operator fun invoke(longitude: Double, latitude: Double) = repository.loading(longitude, latitude)
}