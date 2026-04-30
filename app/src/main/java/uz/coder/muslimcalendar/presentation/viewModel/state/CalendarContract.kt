package uz.coder.muslimcalendar.presentation.viewModel.state

import uz.coder.muslimcalendar.domain.model.Calendar

data class CalendarState(
    val isLoading: Boolean = false,
    val calendarList: List<Calendar> = emptyList(),
    val error: String? = null
)

sealed interface CalendarIntent {
    data object LoadCalendar : CalendarIntent
    data object RefreshCalendar : CalendarIntent
}

sealed interface CalendarEffect {
    data class ShowError(val message: String) : CalendarEffect
}
