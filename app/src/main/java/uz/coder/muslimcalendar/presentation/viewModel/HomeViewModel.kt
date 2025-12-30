package uz.coder.muslimcalendar.presentation.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import uz.coder.muslimcalendar.SharedPref
import uz.coder.muslimcalendar.domain.usecase.LoadingUseCase
import uz.coder.muslimcalendar.domain.usecase.PresentDayUseCase
import uz.coder.muslimcalendar.presentation.viewModel.state.HomeState
import uz.coder.muslimcalendar.todo.isConnected
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val presentDayUseCase: PresentDayUseCase,
    private val loadingUseCase: LoadingUseCase,
    private val sharedPref: SharedPref
) : ViewModel() {

    private val _state = MutableStateFlow<HomeState>(HomeState.Init)
    val state: StateFlow<HomeState> = _state

    init { loadTodayData() }

    private fun loadTodayData() {
        viewModelScope.launch {
            _state.value = HomeState.Loading
            presentDayUseCase().collect { calendar ->
                _state.value = HomeState.Success(calendar)
            }
        }
    }

    fun requestLocation() {
        // Location olish logikasi
        val lat = sharedPref.getFloat("saved_latitude", 41.2995f).toDouble()
        val lon = sharedPref.getFloat("saved_longitude", 69.2401f).toDouble()
        loadInformationFromInternet(lat, lon)
    }

    fun loadInformationFromInternet(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            if (!context.isConnected()) return@launch
            try {
                sharedPref.saveValue("saved_latitude", latitude.toFloat())
                sharedPref.saveValue("saved_longitude", longitude.toFloat())
                loadingUseCase(longitude, latitude)
                loadTodayData()
            } catch (_: Exception) { loadTodayData() }
        }
    }

    fun loadWithSavedLocation() {
        val lat = sharedPref.getFloat("saved_latitude", 41.2995f).toDouble()
        val lon = sharedPref.getFloat("saved_longitude", 69.2401f).toDouble()
        loadInformationFromInternet(lat, lon)
    }

    fun shouldShowLocationDialog(): Boolean = !sharedPref.getBoolean("location_requested", false)
    fun markLocationGranted() = sharedPref.saveValue("location_granted", true)
}