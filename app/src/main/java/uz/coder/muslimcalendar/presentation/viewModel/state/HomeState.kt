package uz.coder.muslimcalendar.presentation.viewModel.state

import uz.coder.muslimcalendar.domain.model.MuslimCalendar

sealed class HomeState {
    data object Init : HomeState()
    data object Loading : HomeState()
    data class Success(val data: MuslimCalendar) : HomeState()
    data class Error(val message: String) : HomeState()
}