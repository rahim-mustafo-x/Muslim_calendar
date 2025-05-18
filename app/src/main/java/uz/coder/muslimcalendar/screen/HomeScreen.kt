package uz.coder.muslimcalendar.screen

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import kotlinx.coroutines.launch
import uz.coder.muslimcalendar.R
import uz.coder.muslimcalendar.models.model.Date
import uz.coder.muslimcalendar.models.model.Item
import uz.coder.muslimcalendar.models.model.Menu
import uz.coder.muslimcalendar.models.model.MenuSetting
import uz.coder.muslimcalendar.models.sealed.Screen.About
import uz.coder.muslimcalendar.models.sealed.Screen.AllahName
import uz.coder.muslimcalendar.models.sealed.Screen.Calendar
import uz.coder.muslimcalendar.models.sealed.Screen.Duo
import uz.coder.muslimcalendar.models.sealed.Screen.Namoz
import uz.coder.muslimcalendar.models.sealed.Screen.Qazo
import uz.coder.muslimcalendar.models.sealed.Screen.Quran
import uz.coder.muslimcalendar.models.sealed.Screen.Settings
import uz.coder.muslimcalendar.models.sealed.Screen.Tasbeh
import uz.coder.muslimcalendar.todo.MONTH
import uz.coder.muslimcalendar.ui.theme.Blue
import uz.coder.muslimcalendar.ui.theme.Light_Blue
import uz.coder.muslimcalendar.ui.view.CalendarTopBar
import uz.coder.muslimcalendar.ui.view.MainButton
import uz.coder.muslimcalendar.viewModel.HomeViewModel
import uz.coder.muslimcalendar.viewModel.state.HomeState

@Composable
fun HomeScreen(modifier: Modifier = Modifier, controller: NavHostController) {
    val viewModel = viewModel<HomeViewModel>()
    val menuList = listOf(
        Menu(
            R.drawable.refresh,
            stringResource(R.string.refresh),
            MenuSetting.Refresh
        ),Menu(
            R.drawable.settings,
            stringResource(R.string.settings),
            MenuSetting.Settings
        ),
        Menu(
            R.drawable.about,
            stringResource(R.string.about),
            MenuSetting.About
        )
    )
    Scaffold(modifier = modifier.fillMaxSize(), topBar = {
        CalendarTopBar(modifier = modifier, list = menuList){
            when(it){
                MenuSetting.Refresh->{
                    viewModel.loadInfo()
                }
                MenuSetting.Settings->{
                    controller.navigate(Settings.route)
                }
                MenuSetting.About->{
                    controller.navigate(About.route)
                }
                else->{}
            }
        }
    }){
        Home(controller = controller, viewModel = viewModel, it)
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalPermissionsApi::class)
@Composable
fun Home(
    controller: NavHostController,
    viewModel: HomeViewModel,
    paddingValues: PaddingValues
) {
    viewModel.itemList()
    var list by remember { mutableStateOf<List<Item>>(emptyList()) }
    var showLoadingDialog by remember { mutableStateOf<Boolean>(false) }
    if (showLoadingDialog){
        LoadingDialog(modifier = Modifier.padding(paddingValues))
    }else{
        if (list == emptyList<Item>()) {
            NoInternetScreen(paddingValues = paddingValues)
        } else {
            Screen(
                paddingValues = paddingValues,
                list = list,
                controller = controller,
                viewModel = viewModel
            )
        }
    }
    LaunchedEffect(viewModel.state) {
        viewModel.state.collect {
            when(it){
                is HomeState.Error -> {
                    showLoadingDialog = false
                }
                HomeState.Init -> {
                    showLoadingDialog = false
                }
                HomeState.Loading -> {
                    showLoadingDialog = true
                }
                is HomeState.Success -> {
                    showLoadingDialog = false
                    list = it.data
                    Log.d(TAG, "Home: ${it.data}")
                }
            }
        }
    }
}

@Composable
fun LoadingDialog(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun NoInternetScreen(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues
) {
    Column(
        modifier
            .fillMaxSize()
            .background(White)
            .padding(paddingValues), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Image(painterResource(R.drawable.no_internet), contentDescription = null, contentScale = ContentScale.Crop, modifier = modifier
            .size(200.dp))
        Text(text = stringResource(R.string.failure), color = Black, fontSize = 25.sp, modifier = modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.CenterHorizontally), textAlign = TextAlign.Center)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Screen(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues,
    list: List<Item>,
    controller: NavHostController,
    viewModel: HomeViewModel
) {
    val pagerState = rememberPagerState {
        list.size
    }
    val scope = rememberCoroutineScope()
    Column(
        modifier = modifier
            .padding(paddingValues)
            .background(White)
    ) {
        Box{
            HorizontalPager(state = pagerState) {
                Card(
                    modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .padding(5.dp), colors = CardDefaults.cardColors(Light_Blue)
                ) {
                    Column(
                        modifier
                            .fillMaxWidth()
                            .height(220.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(list[it].name, color = White, fontSize = 25.sp)
                        Text(list[it].time, color = White, fontSize = 25.sp)
                    }
                }

            }

        }
        ScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            modifier = modifier.fillMaxWidth(), containerColor = White,
            indicator = { Box {} }) {
            listOf(
                stringResource(R.string.bomdod),
                stringResource(R.string.quyoshChiqishi),
                stringResource(R.string.peshin),
                stringResource(R.string.asr),
                stringResource(R.string.quyoshBotishi),
                stringResource(R.string.shom),
                stringResource(R.string.xufton)
            ).forEachIndexed { index, item ->
                Tab(selected = index == pagerState.currentPage, onClick = {
                    scope.launch { pagerState.animateScrollToPage(index) }
                }, text = {
                    Text(item)
                }, selectedContentColor = Blue, unselectedContentColor = Light_Blue)
            }
        }
        Bottom(modifier, controller, viewModel)
    }
}

@Composable
fun Bottom(
    modifier: Modifier,
    controller: NavHostController,
    viewModel: HomeViewModel
) {
    val date by viewModel.day().collectAsState(initial = Date())
    Column(modifier = modifier
        .fillMaxSize()
        .background(White)) {
        Column(modifier
            .fillMaxWidth()
            .weight(2.5f)) {
            Text("${date.weekDay}, ${date.day} - ${MONTH[date.month]};", color = Light_Blue, modifier = modifier.fillMaxWidth(), textAlign = TextAlign.End)
            Text("${date.hijriDay} - ${date.hijriMonth}.", color = Light_Blue, modifier = modifier.fillMaxWidth(), textAlign = TextAlign.End)
        }
        Column(modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .weight(9.5f)) {
            Row(modifier = modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.CenterHorizontally)) {
                MainButton(resId = R.drawable.book, text = stringResource(R.string.blessing)) {
                    controller.navigate(Duo.route)
                }
                MainButton(resId = R.drawable.calendar, text = stringResource(R.string.calendar)) {
                    controller.navigate(Calendar.route)
                }
                MainButton(resId = R.drawable.nine_nine, text = stringResource(R.string.allah)) {
                    controller.navigate(AllahName.route)
                }
            }
            Row(modifier = modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.CenterHorizontally)) {
                MainButton(resId = R.drawable.rosary, text = stringResource(R.string.rosary)) {
                    controller.navigate(Tasbeh.route)
                }
                MainButton(resId = R.drawable.muslim_man, text = stringResource(R.string.orderOfPrayer)) {
                    controller.navigate(Namoz.route)
                }
                MainButton(resId = R.drawable.carpet, text = stringResource(R.string.qazo)) {
                    controller.navigate(Qazo.route)
                }
            }
            Row(modifier = modifier.fillMaxWidth()) {
                MainButton(resId = R.drawable.quran, text = stringResource(R.string.quran)) {
                    controller.navigate(Quran.route)
                }
            }
        }
    }

}
private const val TAG = "HomeScreen"