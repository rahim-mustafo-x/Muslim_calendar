package uz.coder.muslimcalendar.domain.location

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import uz.coder.muslimcalendar.SharedPref
import uz.coder.muslimcalendar.todo.LOCATION_CONFIGURED
import uz.coder.muslimcalendar.todo.SAVED_LATITUDE
import uz.coder.muslimcalendar.todo.SAVED_LONGITUDE

class LocationSupervisor(private val sharedPref: SharedPref) {

    // Memory is the fast source used by Compose, while SharedPreferences remains the
    // durable source restored after process death.
    private val _isLocationConfigured = MutableStateFlow(readConfigurationFromStorage())
    val isLocationConfiguredFlow = _isLocationConfigured.asStateFlow()

    fun isLocationConfigured(): Boolean = _isLocationConfigured.value

    fun refreshFromStorage() {
        _isLocationConfigured.value = readConfigurationFromStorage()
    }

    private fun readConfigurationFromStorage(): Boolean {
        val isConfigured = sharedPref.getBoolean(LOCATION_CONFIGURED, false)
        val lat = sharedPref.getFloat(SAVED_LATITUDE, 0f)
        val lon = sharedPref.getFloat(SAVED_LONGITUDE, 0f)
        
        // Location is considered configured only if the flag is set AND coordinates are non-zero
        // (Assuming 0,0 is not a valid user-selected location for this app's target audience)
        return isConfigured && (lat != 0f || lon != 0f)
    }

    fun markLocationConfigured() {
        sharedPref.saveValue(LOCATION_CONFIGURED, true)
        refreshFromStorage()
    }
    
    fun clearLocationConfig() {
        sharedPref.removeValue(LOCATION_CONFIGURED)
        sharedPref.removeValue(SAVED_LATITUDE)
        sharedPref.removeValue(SAVED_LONGITUDE)
        _isLocationConfigured.value = false
    }
}
