package uz.coder.muslimcalendar.domain.usecase

import uz.coder.muslimcalendar.domain.repository.CalendarRepository
import javax.inject.Inject

class RegionUseCase @Inject constructor(private val repository: CalendarRepository) {
    suspend operator fun invoke(region: String) = repository.region(region)
}