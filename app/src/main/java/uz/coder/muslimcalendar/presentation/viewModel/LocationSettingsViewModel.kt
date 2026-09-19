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
import uz.coder.muslimcalendar.domain.location.LocationSupervisor
import uz.coder.muslimcalendar.domain.repository.CalendarRepository
import uz.coder.muslimcalendar.domain.repository.NotificationScheduler
import uz.coder.muslimcalendar.todo.CALENDAR_DOWNLOAD_CONSENT
import uz.coder.muslimcalendar.todo.SAVED_LATITUDE
import uz.coder.muslimcalendar.todo.SAVED_LONGITUDE

data class LocationSettingsState(
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null,
    val detectedCity: String? = null,
    val detectedLatitude: Double? = null,
    val detectedLongitude: Double? = null,
    val isLocating: Boolean = false
)

class LocationSettingsViewModel(
    private val repository: CalendarRepository,
    private val sharedPref: SharedPref,
    private val notificationScheduler: NotificationScheduler,
    private val locationSupervisor: LocationSupervisor
) : ViewModel() {
    private val _state = MutableStateFlow(LocationSettingsState())
    val state: StateFlow<LocationSettingsState> = _state.asStateFlow()

    fun updateDetectedLocation(name: String, latitude: Double, longitude: Double) {
        _state.update {
            it.copy(
                detectedCity = name,
                detectedLatitude = latitude,
                detectedLongitude = longitude,
                isLocating = false
            )
        }
    }

    fun setLocating(locating: Boolean) {
        _state.update { it.copy(isLocating = locating) }
    }

    fun saveLocation(name: String, latitude: Double, longitude: Double) {
        if (_state.value.isSaving) return

        if (name.isBlank() || latitude !in -90.0..90.0 || longitude !in -180.0..180.0) {
            _state.update { it.copy(error = "Joylashuv ma'lumotlarini tekshiring.") }
            return
        }

        // Optimization check: If new location matches exactly what is already saved, skip clear/reload logic to avoid unnecessary loading indicators
        val currentSavedLat = sharedPref.getFloat(SAVED_LATITUDE, 0f)
        val currentSavedLon = sharedPref.getFloat(SAVED_LONGITUDE, 0f)
        val currentSavedName = sharedPref.getString("region", "")
        if (name == currentSavedName && latitude.toFloat() == currentSavedLat && longitude.toFloat() == currentSavedLon) {
            _state.update { it.copy(isSaved = true) }
            return
        }

        // Mark the operation before starting the coroutine so a rapid second tap cannot
        // enqueue another save/navigation transition.
        _state.update { it.copy(isSaving = true, error = null) }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.region(name)
                sharedPref.saveValue(SAVED_LATITUDE, latitude.toFloat())
                sharedPref.saveValue(SAVED_LONGITUDE, longitude.toFloat())
                
                // Clear any previous consent and data to force a fresh download for the new location
                sharedPref.removeValue(CALENDAR_DOWNLOAD_CONSENT)
                
                // Safe clear or skip invocation to avoid background lock/deadlock freezes on repeat clicks
                try {
                    repository.remove()
                } catch (_: Exception) {}
                
                // Trigger Home screen to react to the new configuration
                locationSupervisor.markLocationConfigured()
                
                _state.update { it.copy(isSaving = false, isSaved = true) }
            } catch (_: Exception) {
                _state.update {
                    it.copy(isSaving = false, error = "Namoz vaqtlarini keyinroq yangilab bo'lmadi.")
                }
            }
        }
    }
}
