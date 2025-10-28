package uz.coder.muslimcalendar.presentation.screen

import androidx.activity.compose.BackHandler
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import uz.coder.muslimcalendar.presentation.ui.view.CalendarTopBar
import uz.coder.muslimcalendar.presentation.ui.view.CalendarView
import uz.coder.muslimcalendar.presentation.viewModel.CalendarViewModel

@Composable
fun CalendarScreen(modifier: Modifier = Modifier, controller: NavHostController) {
    Calendar(modifier, controller)
}

@Composable
fun Calendar(modifier: Modifier, controller: NavHostController) {
    val viewModel = viewModel<CalendarViewModel>()
    Scaffold(topBar = { CalendarTopBar(list = emptyList()) {} }) {
        CalendarView(modifier = modifier, viewModel = viewModel, paddingValues = it)
    }
    BackHandler {
        controller.popBackStack()
    }
}
