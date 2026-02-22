package uz.coder.muslimcalendar.presentation.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import uz.coder.muslimcalendar.domain.model.AllahName
import uz.coder.muslimcalendar.domain.model.sealed.Screen
import uz.coder.muslimcalendar.todo.allahNames
import uz.coder.muslimcalendar.presentation.ui.view.ModernListItem

@Composable
fun AllahNameScreen(modifier: Modifier = Modifier, controller: NavHostController) {
    AllahName(modifier, controller)
}

@Composable
fun AllahName(modifier: Modifier = Modifier, controller: NavHostController) {
    Scaffold(modifier.fillMaxSize()) {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(it)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            itemsIndexed(allahNames) { index, item ->
                Name(index = index, item = item, controller = controller)
            }
        }
    }
}

@Composable
fun Name(index: Int, item: AllahName, controller: NavHostController) {
    ModernListItem(
        title = item.name,
        onClick = { controller.navigate(Screen.AllahNameMeaning.route + "/$index") }
    )
}
