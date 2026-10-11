package uz.coder.muslimcalendar.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import uz.coder.muslimcalendar.domain.model.Calendar
import uz.coder.muslimcalendar.shared.domain.model.MuslimCalendar
import uz.coder.muslimcalendar.domain.usecase.OneMonthDayUseCase
import uz.coder.muslimcalendar.presentation.viewModel.state.CalendarIntent
import uz.coder.muslimcalendar.presentation.viewModel.state.CalendarState
import android.content.Context
import uz.coder.muslimcalendar.R
import androidx.compose.ui.graphics.Color
import uz.coder.muslimcalendar.presentation.ui.theme.Light_Blue

class CalendarViewModel (
     private val context: Context,
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
            oneMonthDayUseCase()
                .map { items ->
                    withContext(Dispatchers.Default) {
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

                        val monthsArray = context.resources.getStringArray(R.array.months)
                        // Add days
                        items.forEach {
                            val monthName = monthsArray.getOrNull(it.month - 1) ?: ""
                            calendarList.add(Calendar("${it.day}-$monthName", Color.White, Light_Blue))
                            calendarList.add(Calendar(it.tongSaharlik, Color.Black, Color.White))
                            calendarList.add(Calendar(it.sunRise, Color.Black, Color.White))
                            calendarList.add(Calendar(it.peshin, Color.Black, Color.White))
                            calendarList.add(Calendar(it.asr, Color.Black, Color.White))
                            calendarList.add(Calendar(it.shomIftor, Color.Black, Color.White))
                            calendarList.add(Calendar(it.hufton, Color.Black, Color.White))
                        }

                        calendarList
                    }
                }
                .collect { calendarList ->
                    _state.update { it.copy(isLoading = false, calendarList = calendarList) }
                }
        }
    }
}
