package uz.coder.muslimcalendar.shared.presentation.navigation

sealed class Destination(val route: String) {
    object Home : Destination("home")
    object Settings : Destination("settings")
}
