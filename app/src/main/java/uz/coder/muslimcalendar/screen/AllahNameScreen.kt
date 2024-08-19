package uz.coder.muslimcalendar.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import uz.coder.muslimcalendar.models.model.AllahName
import uz.coder.muslimcalendar.models.sealed.Screen
import uz.coder.muslimcalendar.todo.allahNames
import uz.coder.muslimcalendar.ui.theme.Light_Blue

@Composable
fun AllahNameScreen(modifier: Modifier = Modifier, controller: NavHostController) {
    AllahName(modifier, controller)
}

@Composable
fun AllahName(modifier: Modifier = Modifier, controller: NavHostController) {
    Scaffold(modifier.fillMaxSize()) {
        LazyColumn(modifier = modifier.fillMaxSize().padding(it)) {
            itemsIndexed(allahNames){index, item->
                Name(index = index, item = item, controller = controller)
            }
        }
    }
}

@Composable
fun Name(modifier: Modifier = Modifier, index: Int, item: AllahName, controller: NavHostController) {
    Card(onClick = { controller.navigate(Screen.AllahNameMeaning.route + "/$index") }, elevation = CardDefaults.elevatedCardElevation(8.dp), modifier = modifier
        .fillMaxWidth()
        .padding(8.dp), colors = CardDefaults.cardColors(Light_Blue)) {
        Text(item.name, modifier = modifier
            .padding(16.dp)
            .fillMaxSize(), color = White, fontSize = 20.sp)
    }
}
