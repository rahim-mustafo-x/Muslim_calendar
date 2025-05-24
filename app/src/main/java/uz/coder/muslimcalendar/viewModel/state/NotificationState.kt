package uz.coder.muslimcalendar.viewModel.state

import uz.coder.muslimcalendar.models.model.Notification

sealed class NotificationState {
    data object Init : NotificationState()
    data object Loading : NotificationState()
    data class Success(val list: List<Notification>) : NotificationState()
}