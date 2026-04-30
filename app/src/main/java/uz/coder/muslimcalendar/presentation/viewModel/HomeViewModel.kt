package uz.coder.muslimcalendar.presentation.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uz.coder.muslimcalendar.SharedPref
import uz.coder.muslimcalendar.domain.repository.NotificationScheduler
import uz.coder.muslimcalendar.domain.usecase.LoadingUseCase
import uz.coder.muslimcalendar.domain.usecase.PresentDayUseCase
import uz.coder.muslimcalendar.presentation.viewModel.state.HomeEffect
import uz.coder.muslimcalendar.presentation.viewModel.state.HomeIntent
import uz.coder.muslimcalendar.presentation.viewModel.state.HomeState
import uz.coder.muslimcalendar.todo.isConnected
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val presentDayUseCase: PresentDayUseCase,
    private val loadingUseCase: LoadingUseCase,
    private val sharedPref: SharedPref,
    private val notificationScheduler: NotificationScheduler
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    private val _effect = Channel<HomeEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        handleIntent(HomeIntent.LoadData)
        startTimer()
    }

    fun handleIntent(intent: HomeIntent) {
        when (intent) {
            HomeIntent.LoadData -> loadTodayData()
            is HomeIntent.UpdateLocation -> updateLocation(intent.latitude, intent.longitude)
            is HomeIntent.OnMenuClick -> viewModelScope.launch { _effect.send(HomeEffect.Navigate(intent.route)) }
        }
    }

    private fun loadTodayData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            presentDayUseCase().collect { calendar ->
                _state.update { it.copy(data = calendar, isLoading = false) }
                calculatePrayerIndex()
            }
        }
    }

    private fun updateLocation(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            if (!context.isConnected()) {
                _effect.send(HomeEffect.ShowError("No internet connection"))
                return@launch
            }
            try {
                sharedPref.saveValue("saved_latitude", latitude.toFloat())
                sharedPref.saveValue("saved_longitude", longitude.toFloat())
                loadingUseCase(longitude, latitude)
                notificationScheduler.scheduleAllAlarms()
            } catch (e: Exception) {
                _effect.send(HomeEffect.ShowError(e.message ?: "Unknown error"))
            }
        }
    }

    private fun startTimer() {
        viewModelScope.launch {
            while (true) {
                _state.update { it.copy(currentTime = LocalTime.now()) }
                calculatePrayerIndex()
                delay(60000)
            }
        }
    }

    private fun calculatePrayerIndex() {
        val calendar = _state.value.data ?: return
        val now = LocalTime.now()
        val adjustments = listOf(
            sharedPref.getInt("adj_bomdod", 0),
            sharedPref.getInt("adj_quyosh", 0),
            sharedPref.getInt("adj_peshin", 0),
            sharedPref.getInt("adj_asr", 0),
            sharedPref.getInt("adj_shom", 0),
            sharedPref.getInt("adj_xufton", 0)
        )

        val prayerTimes = listOf(
            calendar.tongSaharlik,
            calendar.sunRise,
            calendar.peshin,
            calendar.asr,
            calendar.shomIftor,
            calendar.hufton
        ).mapIndexed { index, time ->
            if (time.isEmpty()) return@mapIndexed null
            try {
                val (h, m) = time.split(":").map { it.toInt() }
                LocalTime.of(h, m).plusMinutes(adjustments[index].toLong())
            } catch (e: Exception) {
                null
            }
        }

        var currentIdx = -1
        for (i in prayerTimes.indices) {
            val time = prayerTimes[i] ?: continue
            if (now.isAfter(time)) {
                currentIdx = i
            } else {
                break
            }
        }
        _state.update { it.copy(currentPrayerIndex = currentIdx) }
    }

    fun loadWithSavedLocation() {
        val lat = sharedPref.getFloat("saved_latitude", 41.2995f).toDouble()
        val lon = sharedPref.getFloat("saved_longitude", 69.2401f).toDouble()
        handleIntent(HomeIntent.UpdateLocation(lat, lon))
    }
}
