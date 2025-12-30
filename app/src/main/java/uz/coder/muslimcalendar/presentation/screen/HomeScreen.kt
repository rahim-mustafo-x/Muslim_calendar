package uz.coder.muslimcalendar.presentation.screen

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import android.os.Looper
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import uz.coder.muslimcalendar.R
import uz.coder.muslimcalendar.domain.model.Menu
import uz.coder.muslimcalendar.domain.model.MuslimCalendar
import uz.coder.muslimcalendar.domain.model.sealed.Screen.*
import uz.coder.muslimcalendar.models.model.MenuSetting
import uz.coder.muslimcalendar.presentation.ui.theme.Blue
import uz.coder.muslimcalendar.presentation.ui.theme.Light_Blue
import uz.coder.muslimcalendar.presentation.ui.view.CalendarTopBar
import uz.coder.muslimcalendar.presentation.ui.view.MainButton
import uz.coder.muslimcalendar.presentation.viewModel.HomeViewModel
import uz.coder.muslimcalendar.presentation.viewModel.state.HomeState
import uz.coder.muslimcalendar.todo.toItems

@SuppressLint("MissingPermission")
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    controller: NavHostController,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val homeState by homeViewModel.state.collectAsStateWithLifecycle()

    // 📍 Location faqat 1 marta olinadi
    LaunchedEffect(Unit) {
        val locationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val provider =
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                LocationManager.GPS_PROVIDER
            else
                LocationManager.NETWORK_PROVIDER

        @Suppress("DEPRECATION")
        locationManager.requestSingleUpdate(provider, {
            homeViewModel.loadInformationFromInternet(it.latitude, it.longitude)
        }, Looper.getMainLooper())
    }

    val menuList = listOf(
        Menu(R.drawable.ic_bell, stringResource(R.string.notification), MenuSetting.Notification),
        Menu(R.drawable.about, stringResource(R.string.about), MenuSetting.About)
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CalendarTopBar(
                modifier.fillMaxWidth(),
                text = stringResource(R.string.app_name),
                list = menuList
            ) { menu ->
                when (menu) {
                    MenuSetting.Notification -> controller.navigate(Notification.route)
                    MenuSetting.About -> controller.navigate(About.route)
                    else -> {}
                }
            }
        }
    ) { padding ->
        when (homeState) {
            HomeState.Init, HomeState.Loading -> LoadingScreen(padding)
            is HomeState.Error -> ErrorScreen(
                (homeState as HomeState.Error).message
            ) { homeViewModel.loadWithSavedLocation() }

            is HomeState.Success -> Screen(
                paddingValues = padding,
                muslimCalendar = (homeState as HomeState.Success).data,
                controller = controller
            )
        }
    }
}


@Composable
fun LoadingScreen(padding: PaddingValues) = Box(
    modifier = Modifier
        .fillMaxSize()
        .padding(padding),
    contentAlignment = Alignment.Center
) {
    CircularProgressIndicator()
}

@Composable
fun ErrorScreen(message: String, onRetry: () -> Unit) = Column(
    modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
) {
    Image(
        painterResource(R.drawable.no_internet),
        contentDescription = null,
        modifier = Modifier.size(200.dp),
        contentScale = ContentScale.Crop
    )
    Spacer(Modifier.height(16.dp))
    Text(message, fontSize = 25.sp, textAlign = TextAlign.Center)
    Spacer(Modifier.height(24.dp))
    Button(onClick = onRetry) { Text("Qayta urinish") }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Screen(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues,
    muslimCalendar: MuslimCalendar,
    controller: NavHostController
) {
    val pagerState = rememberPagerState { muslimCalendar.item.size }
    val scope = rememberCoroutineScope()
    val times = listOf(
        stringResource(R.string.bomdod),
        stringResource(R.string.quyoshChiqishi),
        stringResource(R.string.peshin),
        stringResource(R.string.asr),
        stringResource(R.string.shom),
        stringResource(R.string.xufton)
    )
    val myTime = Pair(times, muslimCalendar.item).toItems()

    Column(
        modifier = modifier
            .padding(paddingValues)
            .background(MaterialTheme.colorScheme.background)
    ) {
        HorizontalPager(state = pagerState) { page ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .padding(5.dp),
                colors = CardDefaults.cardColors(Light_Blue)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(myTime[page].name, color = MaterialTheme.colorScheme.onPrimary, fontSize = 25.sp)
                    Text(myTime[page].time, color = MaterialTheme.colorScheme.onPrimary, fontSize = 25.sp)
                }
            }
        }

        ScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            containerColor = MaterialTheme.colorScheme.background,
            indicator = { Box {} }
        ) {
            times.forEachIndexed { index, item ->
                Tab(
                    selected = index == pagerState.currentPage,
                    onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                    text = { Text(item) },
                    selectedContentColor = Blue,
                    unselectedContentColor = Light_Blue
                )
            }
        }

        Bottom(modifier, controller)
    }
}

@Composable
fun Bottom(modifier: Modifier, controller: NavHostController) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            MainButton(modifier = modifier,R.drawable.book, stringResource(R.string.blessing)) { controller.navigate(Duo.route) }
            MainButton(modifier = modifier,R.drawable.calendar, stringResource(R.string.calendar)) { controller.navigate(Calendar.route) }
            MainButton(modifier = modifier,R.drawable.nine_nine, stringResource(R.string.allah)) { controller.navigate(AllahName.route) }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            MainButton(modifier = modifier,R.drawable.rosary, stringResource(R.string.rosary)) { controller.navigate(Tasbeh.route) }
            MainButton(modifier = modifier,R.drawable.muslim_man, stringResource(R.string.orderOfPrayer)) { controller.navigate(Namoz.route) }
            MainButton(modifier = modifier,R.drawable.carpet, stringResource(R.string.qazo)) { controller.navigate(Qazo.route) }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            MainButton(modifier = modifier,R.drawable.quran, stringResource(R.string.quran)) { controller.navigate(Quran.route) }
        }
    }
}