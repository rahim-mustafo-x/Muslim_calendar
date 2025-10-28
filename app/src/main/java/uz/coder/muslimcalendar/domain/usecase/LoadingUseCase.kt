package uz.coder.muslimcalendar.domain.usecase

import uz.coder.muslimcalendar.domain.repository.CalendarRepository

class LoadingUseCase(private val repository: CalendarRepository) {
    suspend operator fun invoke(longitude: Double, latitude: Double) = repository.loading(longitude, latitude)
}