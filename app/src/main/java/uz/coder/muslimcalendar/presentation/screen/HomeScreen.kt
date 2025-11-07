package uz.coder.muslimcalendar.presentation.screen

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch
import uz.coder.muslimcalendar.R
import uz.coder.muslimcalendar.domain.model.Menu
import uz.coder.muslimcalendar.domain.model.MuslimCalendar
import uz.coder.muslimcalendar.domain.model.sealed.Screen.About
import uz.coder.muslimcalendar.domain.model.sealed.Screen.AllahName
import uz.coder.muslimcalendar.domain.model.sealed.Screen.Calendar
import uz.coder.muslimcalendar.domain.model.sealed.Screen.Duo
import uz.coder.muslimcalendar.domain.model.sealed.Screen.Namoz
import uz.coder.muslimcalendar.domain.model.sealed.Screen.Notification
import uz.coder.muslimcalendar.domain.model.sealed.Screen.Qazo
import uz.coder.muslimcalendar.domain.model.sealed.Screen.Quran
import uz.coder.muslimcalendar.domain.model.sealed.Screen.Tasbeh
import uz.coder.muslimcalendar.models.model.MenuSetting
import uz.coder.muslimcalendar.presentation.ui.theme.Blue
import uz.coder.muslimcalendar.presentation.ui.theme.Light_Blue
import uz.coder.muslimcalendar.presentation.ui.view.CalendarTopBar
import uz.coder.muslimcalendar.presentation.ui.view.MainButton
import uz.coder.muslimcalendar.presentation.viewModel.HomeViewModel
import uz.coder.muslimcalendar.presentation.viewModel.NotificationViewModel
import uz.coder.muslimcalendar.presentation.viewModel.state.HomeState
import uz.coder.muslimcalendar.todo.toItems

@SuppressLint("MissingPermission")
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(modifier: Modifier = Modifier, controller: NavHostController) {
    val viewModel = hiltViewModel<HomeViewModel>()
    val notificationViewModel = hiltViewModel<NotificationViewModel>()
    notificationViewModel.setAlarm()
    val permissionState = rememberPermissionState(android.Manifest.permission.POST_NOTIFICATIONS)
    val context = LocalContext.current
    val location = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val isGpsEnabled = location.isProviderEnabled(LocationManager.GPS_PROVIDER)
    val provider = when{
        isGpsEnabled-> LocationManager.GPS_PROVIDER
        else -> LocationManager.NETWORK_PROVIDER
    }

    // Permission dialog ko'rsatiladi (faqat bir marta)
    LaunchedEffect(Unit) {
        permissionState.launchPermissionRequest()
    }

    // Faqat ruxsat berilgan bo'lsa setAlarm chaqiriladi
    LaunchedEffect(permissionState.status) {
        if (permissionState.status.isGranted) {
            notificationViewModel.setAlarm()
        }
    }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME, onEvent = {
        @Suppress("DEPRECATION")
        location.requestSingleUpdate(provider,
            {
                viewModel.loadInformationFromInternet(it.latitude, it.longitude)
            }, Looper.getMainLooper())

    })

    val menuList = listOf(
        Menu(
            R.drawable.ic_bell,
            stringResource(R.string.notification),
            MenuSetting.Notification
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
                MenuSetting.About -> controller.navigate(About.route)
                MenuSetting.Notification -> controller.navigate(Notification.route)
                else -> {}
            }
        }
    }) {
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
    val notificationViewModel = viewModel<NotificationViewModel>()
    viewModel.itemList()
    var muslimCalendar by remember { mutableStateOf(MuslimCalendar()) }
    var showLoadingDialog by remember { mutableStateOf(false) }
    if (showLoadingDialog){
        LoadingDialog(modifier = Modifier.padding(paddingValues))
    }else{
        if (muslimCalendar == MuslimCalendar()) {
            NoInternetScreen(paddingValues = paddingValues)
        } else {
            Screen(
                paddingValues = paddingValues,
                muslimCalendar = muslimCalendar,
                controller = controller
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
                    muslimCalendar = it.data
                    notificationViewModel.setAlarm()
                    Log.d(TAG, "Home Success: ${it.data}")
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
    muslimCalendar: MuslimCalendar,
    controller: NavHostController
) {
    val pagerState = rememberPagerState {
        muslimCalendar.item.size
    }
    val scope = rememberCoroutineScope()
    val list = listOf(
        stringResource(R.string.bomdod),
        stringResource(R.string.quyoshChiqishi),
        stringResource(R.string.peshin),
        stringResource(R.string.asr),
        stringResource(R.string.shom),
        stringResource(R.string.xufton)
    )
    val myTime = Pair(list, muslimCalendar.item).toItems()
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
                        Text(myTime[it].name, color = White, fontSize = 25.sp)
                        Text(myTime[it].time, color = White, fontSize = 25.sp)
                    }
                }

            }

        }
        ScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            modifier = modifier.fillMaxWidth(), containerColor = White,
            indicator = { Box {} }) {
            list.forEachIndexed { index, item ->
                Tab(selected = index == pagerState.currentPage, onClick = {
                    scope.launch { pagerState.animateScrollToPage(index) }
                }, text = {
                    Text(item)
                }, selectedContentColor = Blue, unselectedContentColor = Light_Blue)
            }
        }
        Bottom(modifier, controller)
    }
}

@Composable
fun Bottom(
    modifier: Modifier,
    controller: NavHostController
) {
    Column(modifier = modifier
        .fillMaxSize()
        .background(White)) {
        Column(modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())) {
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