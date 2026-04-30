package uz.coder.muslimcalendar.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uz.coder.muslimcalendar.domain.model.Calendar
import uz.coder.muslimcalendar.domain.usecase.OneMonthDayUseCase
import uz.coder.muslimcalendar.presentation.viewModel.state.CalendarIntent
import uz.coder.muslimcalendar.presentation.viewModel.state.CalendarState
import javax.inject.Inject
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import uz.coder.muslimcalendar.R
import androidx.compose.ui.graphics.Color
import uz.coder.muslimcalendar.presentation.ui.theme.Light_Blue

@HiltViewModel
class CalendarViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val oneMonthDayUseCase: OneMonthDayUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CalendarState())
    val state: StateFlow<CalendarState> = _state.asStateFlow()

    init {
        handleIntent(CalendarIntent.LoadCalendar)
    }

    fun handleIntent(intent: CalendarIntent) {
        when (intent) {
            CalendarIntent.LoadCalendar -> loadCalendar()
            CalendarIntent.RefreshCalendar -> loadCalendar()
        }
    }

    private fun loadCalendar() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            oneMonthDayUseCase().collect { items ->
                val calendarList = mutableListOf<Calendar>()
                
                // Add header
                calendarList.addAll(listOf(
                    Calendar(context.getString(R.string.dayMonth), Color.White, Light_Blue),
                    Calendar(context.getString(R.string.bomdod), Color.White, Light_Blue),
                    Calendar(context.getString(R.string.quyoshChiqishi), Color.White, Light_Blue),
                    Calendar(context.getString(R.string.peshin), Color.White, Light_Blue),
                    Calendar(context.getString(R.string.asr), Color.White, Light_Blue),
                    Calendar(context.getString(R.string.shom), Color.White, Light_Blue),
                    Calendar(context.getString(R.string.xufton), Color.White, Light_Blue)
                ))

                // Add days
                items.forEach {
                    calendarList.add(Calendar(it.day.toString().plus("-${context.resources.getStringArray(R.array.months)[it.month-1]}"), Color.White, Light_Blue))
                    calendarList.add(Calendar(it.tongSaharlik, Color.Black, Color.White))
                    calendarList.add(Calendar(it.sunRise, Color.Black, Color.White))
                    calendarList.add(Calendar(it.peshin, Color.Black, Color.White))
                    calendarList.add(Calendar(it.asr, Color.Black, Color.White))
                    calendarList.add(Calendar(it.shomIftor, Color.Black, Color.White))
                    calendarList.add(Calendar(it.hufton, Color.Black, Color.White))
                }

                _state.update { it.copy(isLoading = false, calendarList = calendarList) }
            }
        }
    }
}
