package uz.coder.muslimcalendar.presentation.viewModel.state

import uz.coder.muslimcalendar.domain.model.MuslimCalendar
import java.time.LocalTime

data class HomeState(
    val isLoading: Boolean = false,
    val data: MuslimCalendar? = null,
    val currentPrayerIndex: Int = -1,
    val currentTime: LocalTime = LocalTime.now(),
    val error: String? = null
)

sealed interface HomeIntent {
    data object LoadData : HomeIntent
    data class UpdateLocation(val latitude: Double, val longitude: Double) : HomeIntent
    data class OnMenuClick(val route: String) : HomeIntent
}

sealed interface HomeEffect {
    data class Navigate(val route: String) : HomeEffect
    data class ShowError(val message: String) : HomeEffect
}
