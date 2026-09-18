package uz.coder.muslimcalendar.presentation.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import uz.coder.muslimcalendar.domain.model.sealed.Screen
import uz.coder.muslimcalendar.todo.allahNames
import uz.coder.muslimcalendar.presentation.ui.view.SelectionListScreen

@Composable
fun AllahNameScreen(modifier: Modifier = Modifier, controller: NavHostController) {
    SelectionListScreen(
        title = "Allohning 99 ismi",
        items = allahNames,
        controller = controller,
        itemTitle = { it.name },
        onItemClick = { controller.navigate(Screen.AllahNameMeaning.route + "/$it") },
        modifier = modifier
    )
}
