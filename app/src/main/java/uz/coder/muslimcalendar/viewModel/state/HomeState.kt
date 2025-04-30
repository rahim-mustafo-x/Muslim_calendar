package uz.coder.muslimcalendar.viewModel.state

import uz.coder.muslimcalendar.models.model.Item

sealed class HomeState {
    data object Init : HomeState()
    data object Loading : HomeState()
    data class Success(val data: List<Item>) : HomeState()
    data class Error(val message: String) : HomeState()
}