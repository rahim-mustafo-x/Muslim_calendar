package uz.coder.muslimcalendar.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import uz.coder.muslimcalendar.R
import uz.coder.muslimcalendar.ui.theme.Dark_Green
import uz.coder.muslimcalendar.ui.theme.Light_Green
import uz.coder.muslimcalendar.ui.view.CalendarTopBar
import uz.coder.muslimcalendar.viewModel.CalendarViewModel

@Composable
fun HomeScreen(modifier: Modifier = Modifier, controller: NavHostController) {
    val viewModel = viewModel<CalendarViewModel>()
    Scaffold(modifier = modifier.fillMaxSize(), topBar = {
        CalendarTopBar(controller = controller, modifier = modifier, viewModel = viewModel)
    }){
        Home(modifier = modifier, controller = controller, viewModel = viewModel, it)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Home(
    modifier: Modifier = Modifier,
    controller: NavHostController,
    viewModel: CalendarViewModel,
    paddingValues: PaddingValues
) {
    val list by viewModel.timeList().collectAsState(emptyList())
    val pagerState = rememberPagerState {
        list.size
    }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val state = rememberScrollState()
    Column(modifier = modifier
        .padding(paddingValues)
        .verticalScroll(state)) {
        HorizontalPager(state = pagerState) {
            Card(
                modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .padding(10.dp), colors = CardDefaults.cardColors(Dark_Green)) {
                Column(
                    modifier
                        .fillMaxWidth()
                        .height(220.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Text(list[it].name, color = White, fontSize = 25.sp)
                    Text(list[it].time, color = White, fontSize = 25.sp)
                }
            }

        }
        ScrollableTabRow(selectedTabIndex = pagerState.currentPage, modifier = modifier.fillMaxWidth(), indicator = { Box(modifier){} }) {
            mutableListOf(context.getString(R.string.bomdod), context.getString(R.string.quyosh), context.getString(R.string.peshin), context.getString(R.string.asr), context.getString(R.string.shom), context.getString(R.string.xufton)).forEachIndexed { index, item ->
                Tab(selected = index == pagerState.currentPage, onClick = {
                    scope.launch { pagerState.animateScrollToPage(index) }
                }, text = {
                    Text(item)
                 }, selectedContentColor = Light_Green, unselectedContentColor = Dark_Green)
                }
        }
        Bottom(modifier, controller, viewModel)
    }
}

@Composable
fun Bottom(modifier: Modifier, controller: NavHostController, viewModel: CalendarViewModel) {
    Box(modifier.fillMaxSize()) {
        Image(painter = painterResource(id = R.drawable.bottom_img), contentDescription = null, contentScale = ContentScale.Crop, modifier = modifier.fillMaxSize())
        Column(modifier = modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Row(modifier = modifier.fillMaxSize(), horizontalArrangement = Arrangement.Absolute.Center, verticalAlignment = Alignment.CenterVertically) {
                Card(onClick = {  }, shape = CardDefaults.elevatedShape, modifier = modifier.size(80.dp). padding(5.dp), colors = CardDefaults.cardColors(
                    Dark_Green)) {
                    Image(painterResource(R.drawable.book), contentDescription = null, modifier = modifier
                        .fillMaxSize()
                        .padding(2.5.dp), contentScale = ContentScale.Crop)
                }
                Card(onClick = {  }, shape = CardDefaults.elevatedShape, modifier = modifier.size(80.dp). padding(5.dp), colors = CardDefaults.cardColors(
                    Dark_Green)) {
                    Image(painterResource(R.drawable.quran), contentDescription = null, modifier = modifier
                        .fillMaxSize()
                        .padding(2.5.dp), contentScale = ContentScale.Crop)
                }
            }
            Row(modifier = modifier.fillMaxSize(), horizontalArrangement = Arrangement.Absolute.Center, verticalAlignment = Alignment.CenterVertically) {
                Card(onClick = {  }, shape = CardDefaults.elevatedShape, modifier = modifier.size(80.dp). padding(5.dp), colors = CardDefaults.cardColors(
                    Dark_Green)) {
                    Image(painterResource(R.drawable.nine_nine), contentDescription = null, modifier = modifier
                        .fillMaxSize()
                        .padding(2.5.dp), contentScale = ContentScale.Crop)
                }
                Card(onClick = {  }, shape = CardDefaults.elevatedShape, modifier = modifier.size(80.dp). padding(5.dp), colors = CardDefaults.cardColors(
                    Dark_Green)) {
                    Image(painterResource(R.drawable.muslim_man), contentDescription = null, modifier = modifier
                        .fillMaxSize()
                        .padding(2.5.dp), contentScale = ContentScale.Crop)
                }
            }
        }
    }
}
