@file:Suppress("TYPE_INTERSECTION_AS_REIFIED_WARNING")

package uz.coder.muslimcalendar.presentation.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import uz.coder.muslimcalendar.R
import uz.coder.muslimcalendar.domain.model.sealed.Screen
import uz.coder.muslimcalendar.domain.model.sealed.Screen.Tasbeh
import uz.coder.muslimcalendar.domain.model.sealed.Screen.Calendar
import uz.coder.muslimcalendar.domain.model.sealed.Screen.About
import uz.coder.muslimcalendar.domain.model.sealed.Screen.AllahName
import uz.coder.muslimcalendar.domain.model.sealed.Screen.AllahNameMeaning
import uz.coder.muslimcalendar.domain.model.sealed.Screen.Home
import uz.coder.muslimcalendar.domain.model.sealed.Screen.Quran
import uz.coder.muslimcalendar.domain.model.sealed.Screen.QuranAyah
import uz.coder.muslimcalendar.presentation.screen.AboutScreen
import uz.coder.muslimcalendar.presentation.screen.AdvancedSettingsScreen
import uz.coder.muslimcalendar.presentation.screen.AllahNameMeaningScreen
import uz.coder.muslimcalendar.presentation.screen.AllahNameScreen
import uz.coder.muslimcalendar.presentation.screen.CalendarScreen
import uz.coder.muslimcalendar.presentation.screen.DuoMeaningScreen
import uz.coder.muslimcalendar.presentation.screen.DuoScreen
import uz.coder.muslimcalendar.presentation.screen.NamozMeaningScreen
import uz.coder.muslimcalendar.presentation.screen.NamozScreen
import uz.coder.muslimcalendar.presentation.screen.NotificationScreen
import uz.coder.muslimcalendar.presentation.screen.PrayerStatisticsScreen
import uz.coder.muslimcalendar.presentation.screen.QiblaCompassScreen
import uz.coder.muslimcalendar.presentation.screen.QuranAyahScreen
import uz.coder.muslimcalendar.presentation.screen.QuranScreen
import uz.coder.muslimcalendar.presentation.screen.SettingsScreen
import uz.coder.muslimcalendar.presentation.screen.TasbehScreen
import uz.coder.muslimcalendar.presentation.screen.HomeScreen
import uz.coder.muslimcalendar.presentation.screen.LocationSettingsScreen
import uz.coder.muslimcalendar.presentation.screen.MoreMenuScreen
import uz.coder.muslimcalendar.presentation.screen.MoreAppsScreen
import uz.coder.muslimcalendar.presentation.viewModel.HomeViewModel
import org.koin.androidx.compose.koinViewModel
import uz.coder.muslimcalendar.todo.ALLAH_NAME_INDEX
import uz.coder.muslimcalendar.todo.DUO_INDEX
import uz.coder.muslimcalendar.todo.NAMOZ_INDEX
import uz.coder.muslimcalendar.todo.NUMBER

@Composable
fun CalendarNavigation(modifier: Modifier = Modifier) {
    val controller = rememberNavController()
    val navBackStackEntry by controller.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomBarScreens = listOf(Home.route, Quran.route, Screen.QiblaCompass.route, "more_menu")

    Scaffold(
        bottomBar = {
            if (currentRoute in bottomBarScreens) {
                NavigationBar {
                    NavigationBarItem(
                        selected = currentRoute == Home.route,
                        onClick = {
                            if (currentRoute != Home.route) {
                                controller.navigate(Home.route) {
                                    popUpTo(Home.route) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Bosh sahifa") },
                        label = { Text("Bosh sahifa") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == Quran.route,
                        onClick = {
                            if (currentRoute != Quran.route) {
                                controller.navigate(Quran.route) {
                                    popUpTo(Home.route) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = { Icon(painter = painterResource(id = R.drawable.quran), contentDescription = "Qur'on", modifier = Modifier.size(24.dp)) },
                        label = { Text("Qur'on") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == Screen.QiblaCompass.route,
                        onClick = {
                            if (currentRoute != Screen.QiblaCompass.route) {
                                controller.navigate(Screen.QiblaCompass.route) {
                                    popUpTo(Home.route) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = { Icon(painter = painterResource(id = R.drawable.ic_kaaba), contentDescription = "Qibla", modifier = Modifier.size(24.dp)) },
                        label = { Text("Qibla") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == "more_menu",
                        onClick = {
                            if (currentRoute != "more_menu") {
                                controller.navigate("more_menu") {
                                    popUpTo(Home.route) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = { Icon(Icons.Default.MoreHoriz, contentDescription = "Yana") },
                        label = { Text("Yana") }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = controller,
            startDestination = Home.route,
            modifier = modifier.fillMaxSize().padding(paddingValues)
        ) {
            composable(Home.route){
                val viewModel: HomeViewModel = koinViewModel()
                HomeScreen(viewModel = viewModel) { route ->
                    val targetRoute = when (route) {
                        "duo" -> Screen.Duo.route
                        "quran" -> Quran.route
                        "tasbeh" -> Tasbeh.route
                        "qibla" -> Screen.QiblaCompass.route
                        "calendar" -> Calendar.route
                        "settings" -> Screen.Settings.route
                        "location_settings" -> Screen.LocationSettings.route
                        "allah_names" -> AllahName.route
                        else -> route
                    }
                    controller.navigate(targetRoute)
                }
            }
            composable(Tasbeh.route){
                TasbehScreen(controller = controller)
            }
            composable(Screen.Duo.route){
                DuoScreen(controller = controller)
            }
            composable(
                Screen.DuoMeaning.route+"/{$DUO_INDEX}", arguments = arrayListOf(navArgument(
                DUO_INDEX
            ){ type = NavType.IntType })){
                DuoMeaningScreen(controller = controller, navBackStackEntry = it)
            }
            composable(Screen.Namoz.route){
                NamozScreen(controller = controller)
            }
            composable(
                Screen.NamozMeaning.route+"/{$NAMOZ_INDEX}", arguments = arrayListOf(navArgument(
                NAMOZ_INDEX
                ){ type = NavType.IntType })){
                NamozMeaningScreen(modifier, controller = controller, navBackStackEntry = it)
            }
            composable(Calendar.route){
                CalendarScreen(controller = controller)
            }
            composable(About.route){
                AboutScreen()
            }
            composable(AllahName.route){
                AllahNameScreen(controller = controller)
            }
            composable(AllahNameMeaning.route+"/{$ALLAH_NAME_INDEX}", arguments = arrayListOf(navArgument(
                ALLAH_NAME_INDEX
            ){ type = NavType.IntType })){
                AllahNameMeaningScreen(navBackStackEntry =  it)
            }
            composable(Quran.route) {
                QuranScreen(controller = controller)
            }
            composable(QuranAyah.route+"/{$NUMBER}", arguments = arrayListOf(navArgument(NUMBER){ type =
                NavType.IntType })) {
                QuranAyahScreen(controller = controller, navBackStackEntry = it)
            }
            composable(Screen.Notification.route){
                NotificationScreen()
            }
            composable(Screen.Settings.route){
                SettingsScreen(controller = controller)
            }
            composable(Screen.LocationSettings.route) {
                LocationSettingsScreen(controller = controller)
            }
            composable(Screen.AdvancedSettings.route){
                AdvancedSettingsScreen(controller = controller)
            }
            composable(Screen.QiblaCompass.route){
                QiblaCompassScreen(controller = controller)
            }
            composable(Screen.PrayerStatistics.route){
                PrayerStatisticsScreen(controller = controller)
            }
            composable("more_menu") {
                MoreMenuScreen(controller = controller)
            }
            composable("more_apps") {
                MoreAppsScreen(controller = controller)
            }
        }
    }
}
