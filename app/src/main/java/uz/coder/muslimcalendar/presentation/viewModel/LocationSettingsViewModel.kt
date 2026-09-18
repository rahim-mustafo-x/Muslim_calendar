package uz.coder.muslimcalendar.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uz.coder.muslimcalendar.SharedPref
import uz.coder.muslimcalendar.domain.repository.CalendarRepository
import uz.coder.muslimcalendar.domain.repository.NotificationScheduler
import uz.coder.muslimcalendar.todo.LOCATION_CONFIGURED
import uz.coder.muslimcalendar.todo.SAVED_LATITUDE
import uz.coder.muslimcalendar.todo.SAVED_LONGITUDE

data class LocationSettingsState(
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
)

class LocationSettingsViewModel(
    private val repository: CalendarRepository,
    private val sharedPref: SharedPref,
    private val notificationScheduler: NotificationScheduler
) : ViewModel() {
    private val _state = MutableStateFlow(LocationSettingsState())
    val state: StateFlow<LocationSettingsState> = _state.asStateFlow()

    fun saveLocation(name: String, latitude: Double, longitude: Double) {
        if (name.isBlank() || latitude !in -90.0..90.0 || longitude !in -180.0..180.0) {
            _state.update { it.copy(error = "Joylashuv ma'lumotlarini tekshiring.") }
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            _state.update { it.copy(isSaving = true, error = null) }
            try {
                repository.region(name)
                sharedPref.saveValue(SAVED_LATITUDE, latitude.toFloat())
                sharedPref.saveValue(SAVED_LONGITUDE, longitude.toFloat())
                sharedPref.saveValue(LOCATION_CONFIGURED, true)
                // Saving is instant; the calendar refresh continues in the background.
                _state.update { it.copy(isSaving = false, isSaved = true) }

                // Existing calendar entries belong to the former place and must not be reused.
                repository.remove()
                // Home shows a clear first-time download explanation before using the network.
            } catch (error: Exception) {
                _state.update {
                    it.copy(isSaving = false, error = "Namoz vaqtlarini keyinroq yangilab bo'lmadi.")
                }
            }
        }
    }
}
