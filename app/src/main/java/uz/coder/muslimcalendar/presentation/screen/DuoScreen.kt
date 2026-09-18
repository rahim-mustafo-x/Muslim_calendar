package uz.coder.muslimcalendar.presentation.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import uz.coder.muslimcalendar.domain.model.sealed.Screen
import uz.coder.muslimcalendar.presentation.ui.view.SelectionListScreen
import uz.coder.muslimcalendar.todo.dualist

@Composable
fun DuoScreen(modifier: Modifier = Modifier, controller: NavHostController) {
    SelectionListScreen(
        title = "Zikrlar va duolar",
        items = dualist,
        controller = controller,
        itemTitle = { it.name },
        onItemClick = { controller.navigate(Screen.DuoMeaning.route + "/$it") },
        modifier = modifier
    )
}
