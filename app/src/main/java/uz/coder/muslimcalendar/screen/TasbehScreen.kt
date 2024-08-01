package uz.coder.muslimcalendar.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import uz.coder.muslimcalendar.R
import uz.coder.muslimcalendar.models.model.Menu
import uz.coder.muslimcalendar.models.model.MenuScreen
import uz.coder.muslimcalendar.todo.ALL_TASBEH
import uz.coder.muslimcalendar.todo.TASBEH
import uz.coder.muslimcalendar.ui.theme.Light_Blue
import uz.coder.muslimcalendar.ui.view.CalendarTopBar
import uz.coder.muslimcalendar.viewModel.CalendarViewModel

@Composable
fun TasbehScreen(modifier: Modifier = Modifier, controller: NavHostController) {
    Tasbeh(modifier, controller)
}

@Composable
fun Tasbeh(modifier: Modifier = Modifier, controller: NavHostController) {
    val viewModel = viewModel<CalendarViewModel>()
    viewModel.fromPreferencesTasbeh()
    val list = listOf(Menu(R.drawable.refresh, MenuScreen.RefreshTasbeh))
    Scaffold(topBar = { CalendarTopBar(list = list) {
        when(it){
            MenuScreen.RefreshTasbeh->{
                viewModel.refreshTasbehAndAllTasbeh()
                allTasbeh = viewModel.allTasbeh.value
                tasbeh = viewModel.tasbeh.value
            }
            else ->{}
        }
    } }) {
        Screen(modifier, it, controller, viewModel)
    }
}
var allTasbeh by
    mutableIntStateOf(0)
var tasbeh by
    mutableIntStateOf(0)
@Composable
fun Screen(modifier: Modifier = Modifier, paddingValues: PaddingValues, controller: NavHostController, viewModel: CalendarViewModel) {

    LaunchedEffect(true) {
        allTasbeh = viewModel.allTasbeh.value
        tasbeh = viewModel.tasbeh.value
    }
    Column(modifier = modifier
        .fillMaxSize()
        .padding(paddingValues)) {
        Box(modifier = modifier
            .height(220.dp)
            .fillMaxWidth()

            .background(Light_Blue), contentAlignment = Alignment.BottomEnd){
            Text(stringResource(R.string.all).plus(allTasbeh.toString()),
                modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.BottomEnd)
                    .padding(20.dp), White, 30.sp, textAlign = TextAlign.End)
        }
        Box(modifier = modifier
            .fillMaxSize(), contentAlignment = Alignment.Center) {
            OutlinedCard(onClick = {
                if (tasbeh==33){
                    tasbeh = 0
                    viewModel.saveTasbeh(0)
                }
                else viewModel.saveTasbeh(++tasbeh)
                viewModel.saveAllTasbeh(++allTasbeh) }, modifier = modifier.size(180.dp), shape = CircleShape, colors = CardDefaults.cardColors(Light_Blue)) {
                Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                    Text(tasbeh.toString(), color = White, fontSize = 40.sp)
                }
            }
        }
    }
    BackHandler {
        viewModel.saveInt(TASBEH, tasbeh)
        viewModel.saveInt(ALL_TASBEH, allTasbeh)
        controller.popBackStack()
    }
}
