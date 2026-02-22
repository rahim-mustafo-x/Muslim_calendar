package uz.coder.muslimcalendar.presentation.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import uz.coder.muslimcalendar.domain.model.Namoz
import uz.coder.muslimcalendar.todo.namozList
import uz.coder.muslimcalendar.domain.model.sealed.Screen.NamozMeaning
import uz.coder.muslimcalendar.presentation.ui.view.ModernListItem

@Composable
fun NamozScreen(modifier: Modifier = Modifier, controller: NavHostController) {
    Namoz(modifier, controller)
}

@Composable
fun Namoz(modifier: Modifier, controller: NavHostController) {
    Scaffold(modifier.fillMaxSize()) {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(it)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            itemsIndexed(namozList) { index, item ->
                NamozItem(item = item, index = index, controller = controller)
            }
        }
    }
    BackHandler {
        controller.popBackStack()
    }
}

@Composable
fun NamozItem(item: Namoz, index: Int, controller: NavHostController) {
    ModernListItem(
        title = item.name,
        onClick = { controller.navigate(NamozMeaning.route + "/$index") }
    )
}