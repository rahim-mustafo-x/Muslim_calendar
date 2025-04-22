package uz.coder.muslimcalendar.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import uz.coder.muslimcalendar.viewModel.CalendarViewModel

@Composable
fun QuranScreen(modifier: Modifier = Modifier, controller: NavHostController) {
    val viewModel = viewModel<CalendarViewModel>()

}