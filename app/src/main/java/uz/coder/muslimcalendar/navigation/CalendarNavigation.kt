package uz.coder.muslimcalendar.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import uz.coder.muslimcalendar.models.sealed.Screen
import uz.coder.muslimcalendar.models.sealed.Screen.Tasbeh
import uz.coder.muslimcalendar.models.sealed.Screen.Calendar
import uz.coder.muslimcalendar.models.sealed.Screen.About
import uz.coder.muslimcalendar.models.sealed.Screen.AllahName
import uz.coder.muslimcalendar.models.sealed.Screen.AllahNameMeaning
import uz.coder.muslimcalendar.models.sealed.Screen.Home
import uz.coder.muslimcalendar.models.sealed.Screen.Qazo
import uz.coder.muslimcalendar.models.sealed.Screen.Quran
import uz.coder.muslimcalendar.models.sealed.Screen.QuranAyah
import uz.coder.muslimcalendar.screen.AboutScreen
import uz.coder.muslimcalendar.screen.AllahNameMeaningScreen
import uz.coder.muslimcalendar.screen.AllahNameScreen
import uz.coder.muslimcalendar.screen.CalendarScreen
import uz.coder.muslimcalendar.screen.DuoMeaningScreen
import uz.coder.muslimcalendar.screen.DuoScreen
import uz.coder.muslimcalendar.screen.HomeScreen
import uz.coder.muslimcalendar.screen.NamozMeaningScreen
import uz.coder.muslimcalendar.screen.NamozScreen
import uz.coder.muslimcalendar.screen.QazoScreen
import uz.coder.muslimcalendar.screen.QuranAyahScreen
import uz.coder.muslimcalendar.screen.QuranScreen
import uz.coder.muslimcalendar.screen.TasbehScreen
import uz.coder.muslimcalendar.todo.ALLAH_NAME_INDEX
import uz.coder.muslimcalendar.todo.DUO_INDEX
import uz.coder.muslimcalendar.todo.NAMOZ_INDEX
import uz.coder.muslimcalendar.todo.NUMBER

@Composable
fun CalendarNavigation(modifier: Modifier = Modifier) {
    val controller = rememberNavController()
    NavHost(navController = controller, startDestination = Home.route, modifier = modifier.fillMaxSize()) {
        composable(Home.route){
            HomeScreen(controller = controller)
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
        composable(Qazo.route){
            QazoScreen(controller = controller)
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
            AllahNameMeaningScreen(controller = controller, navBackStackEntry =  it)
        }
        composable(Quran.route) {
            QuranScreen(controller = controller)
        }
        composable(QuranAyah.route+"/{$NUMBER}", arguments = arrayListOf(navArgument(NUMBER){ type =
            NavType.IntType })) {
            QuranAyahScreen(controller = controller, navBackStackEntry = it)
        }
    }
}