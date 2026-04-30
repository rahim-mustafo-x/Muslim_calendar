package uz.coder.muslimcalendar.presentation.screen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import uz.coder.muslimcalendar.R
import uz.coder.muslimcalendar.domain.model.Menu
import uz.coder.muslimcalendar.domain.model.MenuSetting
import uz.coder.muslimcalendar.domain.model.MuslimCalendar
import uz.coder.muslimcalendar.domain.model.sealed.Screen.*
import uz.coder.muslimcalendar.presentation.ui.view.CalendarTopBar
import uz.coder.muslimcalendar.presentation.viewModel.HomeViewModel
import uz.coder.muslimcalendar.presentation.viewModel.state.HomeEffect
import uz.coder.muslimcalendar.presentation.viewModel.state.HomeIntent
import uz.coder.muslimcalendar.presentation.viewModel.state.HomeState
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun SplashScreen(controller: NavHostController) {
    LaunchedEffect(Unit) {
        delay(2000)
        controller.navigate(Home.route) {
            popUpTo(Splash.route) { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                painter = painterResource(id = R.drawable.safa_icon),
                contentDescription = null,
                modifier = Modifier.size(120.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Text(
            text = "Safa",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
        )
    }
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    controller: NavHostController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is HomeEffect.Navigate -> controller.navigate(effect.route)
                is HomeEffect.ShowError -> {
                    // Show snackbar or toast
                }
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
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.app_name),
                list = menuList
            ) { menu ->
                val route = when (menu) {
                    MenuSetting.Notification -> Notification.route
                    MenuSetting.About -> About.route
                    MenuSetting.Settings -> Settings.route
                    else -> ""
                }
                if (route.isNotEmpty()) viewModel.handleIntent(HomeIntent.OnMenuClick(route))
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            if (state.isLoading && state.data == null) {
                LoadingScreen()
            } else if (state.error != null && state.data == null) {
                ErrorScreen(state.error!!) { viewModel.handleIntent(HomeIntent.LoadData) }
            } else {
                HomeContent(state, viewModel)
            }
        }
    }
}

@Composable
fun HomeContent(state: HomeState, viewModel: HomeViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // Hero Section with Circular Progress
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            TimeCircularProgress(state.currentTime)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Current Prayer Info
        state.data?.let { calendar ->
            PrayerStatusCard(calendar, state.currentPrayerIndex)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Menu Grid
        BottomGrid(viewModel)

        Spacer(modifier = Modifier.height(32.dp))
        
        // Brand at the bottom
        Text(
            text = "Safa",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun TimeCircularProgress(currentTime: LocalTime) {
    val totalSeconds = 24 * 60 * 60f
    val currentSeconds = currentTime.toSecondOfDay().toFloat()
    val progress = currentSeconds / totalSeconds
    
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1000),
        label = "timeProgress"
    )

    Box(contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier.size(200.dp),
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 12.dp,
            trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
            strokeCap = StrokeCap.Round
        )
        
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = currentTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = "Bugun",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun PrayerStatusCard(calendar: MuslimCalendar, currentIndex: Int) {
    val prayerNames = listOf("Bomdod", "Quyosh", "Peshin", "Asr", "Shom", "Xufton")
    val prayerTimes = calendar.item
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            prayerNames.forEachIndexed { index, name ->
                val isCurrent = index == currentIndex
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (isCurrent) MaterialTheme.colorScheme.primary else Color.Transparent)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = name,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                            color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = prayerTimes[index],
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                        color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                }
                if (index < prayerNames.size - 1) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                }
            }
        }
    }
}

@Composable
fun BottomGrid(viewModel: HomeViewModel) {
    val items = listOf(
        GridItem(Icons.AutoMirrored.Filled.MenuBook, stringResource(R.string.blessing)) { viewModel.handleIntent(HomeIntent.OnMenuClick(Duo.route)) },
        GridItem(Icons.Default.CalendarMonth, stringResource(R.string.calendar)) { viewModel.handleIntent(HomeIntent.OnMenuClick(Calendar.route)) },
        GridItem(Icons.Default.Stars, stringResource(R.string.allah)) { viewModel.handleIntent(HomeIntent.OnMenuClick(AllahName.route)) },
        GridItem(Icons.Default.Spa, stringResource(R.string.rosary)) { viewModel.handleIntent(HomeIntent.OnMenuClick(Tasbeh.route)) },
        GridItem(Icons.Default.SelfImprovement, stringResource(R.string.orderOfPrayer)) { viewModel.handleIntent(HomeIntent.OnMenuClick(Namoz.route)) },
        GridItem(Icons.Default.BarChart, "Statistika") { viewModel.handleIntent(HomeIntent.OnMenuClick(PrayerStatistics.route)) },
        GridItem(Icons.Default.AutoStories, stringResource(R.string.quran)) { viewModel.handleIntent(HomeIntent.OnMenuClick(Quran.route)) },
        GridItem(Icons.Default.Explore, "Qibla") { viewModel.handleIntent(HomeIntent.OnMenuClick(QiblaCompass.route)) },
        GridItem(Icons.Default.Settings, "Sozlamalar") { viewModel.handleIntent(HomeIntent.OnMenuClick(Settings.route)) }
    )

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        for (i in items.indices step 3) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                for (j in 0 until 3) {
                    if (i + j < items.size) {
                        val item = items[i + j]
                        MenuCard(
                            modifier = Modifier.weight(1f),
                            icon = item.icon,
                            text = item.text,
                            onClick = item.onClick
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

data class GridItem(val icon: ImageVector, val text: String, val onClick: () -> Unit)

@Composable
fun MenuCard(modifier: Modifier = Modifier, icon: ImageVector, text: String, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = modifier.height(100.dp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = text, style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center, maxLines = 2, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun LoadingScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorScreen(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = message, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) { Text("Qayta urinish") }
    }
}
