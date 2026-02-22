package uz.coder.muslimcalendar.presentation.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import uz.coder.muslimcalendar.domain.model.MenuSetting
import uz.coder.muslimcalendar.domain.model.MuslimCalendar
import uz.coder.muslimcalendar.domain.model.sealed.Screen.About
import uz.coder.muslimcalendar.domain.model.sealed.Screen.AllahName
import uz.coder.muslimcalendar.domain.model.sealed.Screen.Calendar
import uz.coder.muslimcalendar.domain.model.sealed.Screen.Duo
import uz.coder.muslimcalendar.domain.model.sealed.Screen.Namoz
import uz.coder.muslimcalendar.domain.model.sealed.Screen.Notification
import uz.coder.muslimcalendar.domain.model.sealed.Screen.Quran
import uz.coder.muslimcalendar.domain.model.sealed.Screen.Tasbeh
import uz.coder.muslimcalendar.presentation.ui.view.CalendarTopBar
import uz.coder.muslimcalendar.presentation.ui.view.MainButton
import uz.coder.muslimcalendar.presentation.viewModel.HomeViewModel
import uz.coder.muslimcalendar.presentation.viewModel.state.HomeState
import uz.coder.muslimcalendar.todo.toItems

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    controller: NavHostController,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val homeState by homeViewModel.state.collectAsStateWithLifecycle()
    homeViewModel.loadWithSavedLocation()
    
    LaunchedEffect(Unit) {
        // Check if location permissions are granted before requesting location
        val hasLocationPermission = androidx.core.content.ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED ||
        androidx.core.content.ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED

        if (hasLocationPermission) {
            try {
                val fusedLocationClient = com.google.android.gms.location.LocationServices
                    .getFusedLocationProviderClient(context)
                
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        homeViewModel.loadInformationFromInternet(location.latitude, location.longitude)
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("HomeScreen", "Error getting location: ${e.message}")
            }
        }
    }

    val menuList = listOf(
        Menu(R.drawable.ic_bell, stringResource(R.string.notification), MenuSetting.Notification),
        Menu(R.drawable.ic_about, stringResource(R.string.about), MenuSetting.About),
        Menu(R.drawable.ic_settings, stringResource(R.string.settings), MenuSetting.Settings)
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
                    MenuSetting.Settings -> controller.navigate(uz.coder.muslimcalendar.domain.model.sealed.Screen.Settings.route)
                    else -> {}
                }
            }
        }
    ) { padding ->
        when (homeState) {
            HomeState.Init -> {}
            HomeState.Loading -> LoadingScreen(padding)
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
fun LoadingScreen(padding: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 4.dp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Yuklanmoqda...",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
fun ErrorScreen(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.CloudOff,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = message,
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(Icons.Default.Refresh, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Qayta urinish")
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Screen(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues,
    muslimCalendar: MuslimCalendar,
    controller: NavHostController
) {
    val pagerState = rememberPagerState(pageCount = { muslimCalendar.item.size })
    val scope = rememberCoroutineScope()
    val times = remember {
        listOf(
            "Bomdod",
            "Quyosh chiqishi",
            "Peshin",
            "Asr",
            "Shom",
            "Xufton"
        )
    }
    val myTime = remember(muslimCalendar) { Pair(times, muslimCalendar.item).toItems() }

    Column(
        modifier = modifier
            .padding(paddingValues)
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Prayer Time Pager
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                shape = MaterialTheme.shapes.large,
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = myTime[page].name,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        style = MaterialTheme.typography.titleLarge,
                        fontSize = 22.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = myTime[page].time,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.displayMedium,
                        fontSize = 42.sp
                    )
                }
            }
        }

        ScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            modifier = Modifier.fillMaxWidth(),
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
            edgePadding = 8.dp,
            divider = {
                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                )
            }
        ) {
            times.forEachIndexed { index, item ->
                Tab(
                    selected = index == pagerState.currentPage,
                    onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                    text = {
                        Text(
                            item,
                            style = MaterialTheme.typography.labelLarge
                        )
                    },
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
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
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            MainButton(
                modifier = modifier.weight(1f),
                icon = Icons.AutoMirrored.Filled.MenuBook,
                text = stringResource(R.string.blessing)
            ) { controller.navigate(Duo.route) }
            MainButton(
                modifier = modifier.weight(1f),
                icon = Icons.Default.CalendarMonth,
                text = stringResource(R.string.calendar)
            ) { controller.navigate(Calendar.route) }
            MainButton(
                modifier = modifier.weight(1f),
                icon = Icons.Default.Stars,
                text = stringResource(R.string.allah)
            ) { controller.navigate(AllahName.route) }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            MainButton(
                modifier = modifier.weight(1f),
                icon = Icons.Default.Spa,
                text = stringResource(R.string.rosary)
            ) { controller.navigate(Tasbeh.route) }
            MainButton(
                modifier = modifier.weight(1f),
                icon = Icons.Default.SelfImprovement,
                text = stringResource(R.string.orderOfPrayer)
            ) { controller.navigate(Namoz.route) }
            MainButton(
                modifier = modifier.weight(1f),
                icon = Icons.Default.BarChart,
                text = "Statistika"
            ) { controller.navigate(uz.coder.muslimcalendar.domain.model.sealed.Screen.PrayerStatistics.route) }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            MainButton(
                modifier = modifier.weight(1f),
                icon = Icons.Default.AutoStories,
                text = stringResource(R.string.quran)
            ) { controller.navigate(Quran.route) }
            MainButton(
                modifier = modifier.weight(1f),
                icon = Icons.Default.Explore,
                text = "Qibla"
            ) { controller.navigate(uz.coder.muslimcalendar.domain.model.sealed.Screen.QiblaCompass.route) }
            MainButton(
                modifier = modifier.weight(1f),
                icon = Icons.Default.Settings,
                text = "Sozlamalar"
            ) { controller.navigate(uz.coder.muslimcalendar.domain.model.sealed.Screen.Settings.route) }
        }
    }
}