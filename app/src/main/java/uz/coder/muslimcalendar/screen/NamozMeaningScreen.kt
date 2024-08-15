package uz.coder.muslimcalendar.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import uz.coder.muslimcalendar.todo.NAMOZ_INDEX
import uz.coder.muslimcalendar.todo.namozList
import uz.coder.muslimcalendar.ui.theme.Light_Blue

@Composable
fun NamozMeaningScreen(modifier: Modifier = Modifier, controller: NavHostController, navBackStackEntry: NavBackStackEntry) {
    NamozMeaning(modifier,controller,navBackStackEntry)
}
@Composable
fun NamozMeaning(
    modifier: Modifier,
    controller: NavHostController,
    navBackStackEntry: NavBackStackEntry
) {
    val index = navBackStackEntry.arguments?.getInt(NAMOZ_INDEX, 0)?:0
    val namoz = namozList[index]
    val state = rememberScrollState()
    Column(modifier = modifier
        .verticalScroll(state)
        .background(Light_Blue)
        .fillMaxSize()) {
        Card(modifier = modifier
            .fillMaxWidth()
            .padding(5.dp)
            .height(180.dp), colors = CardDefaults.cardColors(Color.White), elevation = CardDefaults.cardElevation(10.dp)) {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                Text(namoz.name, color = Color.Black, modifier = modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center), textAlign = TextAlign.Center, fontSize = 40.sp)
            }
        }
        Text(namoz.namoz, color = Color.White, fontSize = 20.sp, modifier = modifier.fillMaxSize())
    }
    BackHandler {
        controller.popBackStack()
    }
}