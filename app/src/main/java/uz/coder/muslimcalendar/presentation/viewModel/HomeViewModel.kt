package uz.coder.muslimcalendar.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import uz.coder.muslimcalendar.SharedPref
import uz.coder.muslimcalendar.domain.location.LocationSupervisor
import uz.coder.muslimcalendar.domain.repository.CalendarRepository
import uz.coder.muslimcalendar.domain.repository.NotificationScheduler
import uz.coder.muslimcalendar.domain.usecase.PresentDayUseCase
import uz.coder.muslimcalendar.presentation.viewModel.HomeIntent
import uz.coder.muslimcalendar.shared.domain.model.MuslimCalendar
import uz.coder.muslimcalendar.todo.REGION
import uz.coder.muslimcalendar.todo.LOCATION_CONFIGURED
import uz.coder.muslimcalendar.todo.CALENDAR_DOWNLOAD_ALLOWED
import uz.coder.muslimcalendar.todo.CALENDAR_DOWNLOAD_CONSENT
import uz.coder.muslimcalendar.todo.SAVED_LATITUDE
import uz.coder.muslimcalendar.todo.SAVED_LONGITUDE
import java.time.format.DateTimeFormatter

data class HomeState(
    val prayerTimes: MuslimCalendar = MuslimCalendar(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val locationName: String = "",
    val isLocationConfigured: Boolean = false,
    val calendarStatus: String? = null,
    val showCalendarDownloadPrompt: Boolean = false
)

class HomeViewModel(
    private val repository: CalendarRepository,
    private val presentDayUseCase: PresentDayUseCase,
    private val sharedPref: SharedPref,
    private val notificationScheduler: NotificationScheduler,
    private val locationSupervisor: LocationSupervisor
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState(
        locationName = sharedPref.getString(REGION),
        isLocationConfigured = locationSupervisor.isLocationConfigured()
    ))
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        observePrayerTimes()
        observeLocationStatus()
        checkCalendarAvailability()
    }

    private fun observeLocationStatus() {
        viewModelScope.launch {
            locationSupervisor.isLocationConfiguredFlow.collect { isConfigured ->
                _state.update { 
                    it.copy(
                        isLocationConfigured = isConfigured,
                        locationName = if (isConfigured) sharedPref.getString(REGION) else it.locationName
                    ) 
                }
                if (isConfigured) checkCalendarAvailability()
            }
        }
    }

    private fun observePrayerTimes() {
        viewModelScope.launch {
            presentDayUseCase().collect { times ->
                _state.update { it.copy(prayerTimes = times, isLoading = false) }
            }
        }
    }

    fun handleIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.UpdateLocation -> {
                updateLocation(intent.latitude, intent.longitude, intent.cityName)
            }
            HomeIntent.LoadPrayerTimes -> {
                observePrayerTimes()
            }
        }
    }

    fun reloadSavedLocation() {
        // Rehydrate the in-memory state from durable storage after process/activity
        // recreation before the UI decides whether to open location setup.
        locationSupervisor.refreshFromStorage()
        _state.update {
            it.copy(
                locationName = sharedPref.getString(REGION),
                isLocationConfigured = locationSupervisor.isLocationConfigured()
            )
        }
        checkCalendarAvailability()
    }

    fun approveCalendarDownload() {
        sharedPref.saveValue(CALENDAR_DOWNLOAD_CONSENT, CALENDAR_DOWNLOAD_ALLOWED)
        _state.update { it.copy(showCalendarDownloadPrompt = false) }
        syncCalendar()
    }

    fun declineCalendarDownload() {
        sharedPref.saveValue(CALENDAR_DOWNLOAD_CONSENT, "declined")
        _state.update { it.copy(showCalendarDownloadPrompt = false) }
    }

    fun requestCalendarDownload() {
        _state.update { it.copy(showCalendarDownloadPrompt = true) }
    }

    private fun checkCalendarAvailability() {
        if (!locationSupervisor.isLocationConfigured()) return
        viewModelScope.launch {
            val availability = repository.getCalendarAvailability()
            if (!availability.needsDownload) {
                _state.update { it.copy(calendarStatus = null, showCalendarDownloadPrompt = false) }
                return@launch
            }
            val lastDate = availability.latestStoredDate?.format(DateTimeFormatter.ofPattern("d MMMM"))
            val status = when {
                lastDate == null -> "Namoz vaqtlarini ko‘rsatish uchun joriy va keyingi oy ma’lumotini yuklab oling."
                !availability.currentMonthReady -> "Saqlangan jadval $lastDate gacha. Hozirgi namoz vaqtlari eskirgan bo‘lishi mumkin."
                else -> "Keyingi oy namoz vaqtlari hali saqlanmagan."
            }
            val allowed = sharedPref.getString(CALENDAR_DOWNLOAD_CONSENT) == CALENDAR_DOWNLOAD_ALLOWED
            _state.update { it.copy(calendarStatus = status, showCalendarDownloadPrompt = !allowed) }
            if (allowed) syncCalendar()
        }
    }

    private fun syncCalendar() {
        if (!locationSupervisor.isLocationConfigured()) return
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true) }
                repository.loading(
                    longitude = sharedPref.getFloat(SAVED_LONGITUDE).toDouble(),
                    latitude = sharedPref.getFloat(SAVED_LATITUDE).toDouble()
                )
                notificationScheduler.rescheduleAll()
                _state.update { it.copy(isLoading = false) }
                checkCalendarAvailability()
            } catch (error: Exception) {
                _state.update { it.copy(isLoading = false, error = "Taqvimni yangilab bo‘lmadi.") }
            }
        }
    }

    private fun updateLocation(latitude: Double, longitude: Double, cityName: String?) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, locationName = cityName ?: it.locationName) }
            try {
                repository.loading(longitude, latitude)
                if (cityName != null) {
                    repository.region(cityName)
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
