package uz.coder.muslimcalendar.presentation.viewModel.state

import uz.coder.muslimcalendar.domain.model.MuslimCalendar
import uz.coder.muslimcalendar.domain.model.Notification

sealed class NotificationState {
    data object Init : NotificationState()
    data object Loading : NotificationState()
    data class Success(val list: List<Notification>, val data: List<MuslimCalendar>) : NotificationState()
}