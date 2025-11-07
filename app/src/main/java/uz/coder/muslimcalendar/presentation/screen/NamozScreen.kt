package uz.coder.muslimcalendar.presentation.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
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
import uz.coder.muslimcalendar.domain.model.Namoz
import uz.coder.muslimcalendar.todo.namozList
import uz.coder.muslimcalendar.presentation.ui.theme.Light_Blue
import uz.coder.muslimcalendar.domain.model.sealed.Screen.NamozMeaning

@Composable
fun NamozScreen(modifier: Modifier = Modifier, controller: NavHostController) {
    Namoz(modifier,controller)
}

@Composable
fun Namoz(modifier: Modifier, controller: NavHostController) {
    Scaffold(modifier.fillMaxSize()) {
        LazyColumn(modifier.fillMaxSize().padding(it)) {
            itemsIndexed(namozList){ index, item ->
                NamozItem(modifier, item, index, controller)
            }
        }
    }
    BackHandler {
        controller.popBackStack()
    }
}
@Composable
fun NamozItem(modifier: Modifier, item: Namoz, index: Int, controller: NavHostController) {
    Card(onClick = { controller.navigate(NamozMeaning.route+"/$index") },
        modifier
            .fillMaxWidth()
            .padding(8.dp), shape = RoundedCornerShape(25.dp), colors = CardDefaults.cardColors(
        Light_Blue
    )) {
        Text(item.name, modifier = modifier
            .padding(16.dp)
            .fillMaxSize(), color = White, fontSize = 20.sp)
    }
}