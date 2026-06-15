package uz.coder.muslimcalendar.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import uz.coder.muslimcalendar.shared.domain.PrayerEngine
import uz.coder.muslimcalendar.domain.model.MuslimCalendar
import uz.coder.muslimcalendar.shared.domain.repository.CalendarRepository

data class HomeState(
    val currentTime: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
    val todayData: MuslimCalendar? = null,
    val tomorrowData: MuslimCalendar? = null,
    val nextPrayer: Pair<String, LocalDateTime>? = null,
    val countdown: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentPrayerIndex: Int = -1
)

sealed class SafaHomeIntent {
    object LoadData : SafaHomeIntent()
    data class OnMenuClick(val route: String) : SafaHomeIntent()
    data class UpdateLocation(val latitude: Double, val longitude: Double) : SafaHomeIntent()
}

class SafaHomeViewModel(
    private val calendarRepository: CalendarRepository,
    private val prayerEngine: PrayerEngine
) : ViewModel() {
    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    private val _effect = kotlinx.coroutines.channels.Channel<String>()
    val effect = _effect.receiveAsFlow()

    init {
        handleIntent(SafaHomeIntent.LoadData)
        startTimer()
    }

    fun handleIntent(intent: SafaHomeIntent) {
        when (intent) {
            SafaHomeIntent.LoadData -> observeData()
            is SafaHomeIntent.OnMenuClick -> viewModelScope.launch { _effect.send(intent.route) }
            is SafaHomeIntent.UpdateLocation -> {
                viewModelScope.launch {
                    try {
                        calendarRepository.refreshPrayerTimes(intent.latitude, intent.longitude)
                    } catch (e: Exception) {
                        _state.update { it.copy(error = e.message ?: "Unknown error") }
                    }
                }
            }
        }
    }

    private fun observeData() {
        _state.update { it.copy(isLoading = true, error = null) }
        calendarRepository.getTodayPrayerTimes()
            .combine(calendarRepository.getTodayPrayerTimes()) { today, tomorrow ->
                today to tomorrow
            }
            .onEach { (today, tomorrow) ->
                _state.update { it.copy(todayData = today, tomorrowData = tomorrow, isLoading = false) }
                updateNextPrayer()
                calculateCurrentPrayerIndex()
            }
            .catch { e ->
                _state.update { it.copy(error = e.message ?: "Unknown error", isLoading = false) }
            }
            .launchIn(viewModelScope)
    }

    private fun startTimer() {
        viewModelScope.launch {
            while (true) {
                val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                _state.update { it.copy(currentTime = now) }
                updateCountdown()
                calculateCurrentPrayerIndex()
                kotlinx.coroutines.delay(1000)
            }
        }
    }

    private fun updateNextPrayer() {
        val today = _state.value.todayData ?: return
        val tomorrow = _state.value.tomorrowData ?: return
        val next = prayerEngine.getNextPrayer(_state.value.currentTime, today, tomorrow)
        _state.update { it.copy(nextPrayer = next) }
    }

    private fun updateCountdown() {
        val next = _state.value.nextPrayer ?: return
        val count = prayerEngine.calculateCountdown(_state.value.currentTime, next.second)
        _state.update { it.copy(countdown = count) }
    }

    private fun calculateCurrentPrayerIndex() {
        val today = _state.value.todayData ?: return
        val now = _state.value.currentTime
        val times = today.items.mapIndexed { index, timeStr ->
            try {
                val (h, m) = timeStr.split(":").map { it.toInt() }
                LocalDateTime(now.year, now.month, now.dayOfMonth, h, m)
            } catch (e: Exception) {
                null
            }
        }

        var currentIndex = -1
        for (i in times.indices) {
            val time = times[i] ?: continue
            if (now >= time) {
                currentIndex = i
            } else {
                break
            }
        }
        _state.update { it.copy(currentPrayerIndex = currentIndex) }
    }
}
