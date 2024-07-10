package uz.coder.muslimcalendar.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import uz.coder.muslimcalendar.todo.regionList
import uz.coder.muslimcalendar.ui.theme.Dark_Green
import uz.coder.muslimcalendar.viewModel.CalendarViewModel

@Composable
fun ChooseRegionScreen(modifier: Modifier = Modifier, controller: NavHostController) {
    ChooseRegion(modifier, controller)
}

@Composable
fun ChooseRegion(modifier: Modifier = Modifier, controller: NavHostController) {
    val viewModel = viewModel<CalendarViewModel>()
    LazyColumn(modifier.fillMaxSize()) {
        itemsIndexed(regionList){_,item->
            Region(item = item, controller = controller, viewModel = viewModel)
        }
    }
}

@Composable
fun Region(
    modifier: Modifier = Modifier,
    item: String,
    controller: NavHostController,
    viewModel: CalendarViewModel
) {
    Card(onClick = { controller.popBackStack(); viewModel.region(item); viewModel.loading() }, modifier = modifier.padding(5.dp).fillMaxWidth(), colors = CardDefaults.cardColors(
        Dark_Green)) {
        Text(text = item, modifier = modifier.padding(16.dp).fillMaxSize(), color = White, fontSize = 20.sp)
    }
}
