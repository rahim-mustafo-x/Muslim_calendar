package uz.coder.muslimcalendar.presentation.viewModel

sealed class HomeIntent {
    data class UpdateLocation(val latitude: Double, val longitude: Double, val cityName: String? = null) : HomeIntent()
    object LoadPrayerTimes : HomeIntent()
}
