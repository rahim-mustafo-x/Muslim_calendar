package uz.coder.muslimcalendar.presentation.screen

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.navigation.NavHostController
import uz.coder.muslimcalendar.presentation.viewModel.CalendarViewModel
import uz.coder.muslimcalendar.todo.ALL_TASBEH
import uz.coder.muslimcalendar.todo.TASBEH

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasbehScreen(modifier: Modifier = Modifier, controller: NavHostController) {
    val viewModel = hiltViewModel<CalendarViewModel>()
    viewModel.fromPreferencesTasbeh()
    
    var allTasbeh by remember { mutableIntStateOf(0) }
    var tasbeh by remember { mutableIntStateOf(0) }
    var scale by remember { mutableFloatStateOf(1f) }
    
    val scaleAnimation by animateFloatAsState(
        targetValue = scale,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )
    
    LaunchedEffect(Unit) {
        allTasbeh = viewModel.allTasbeh.value
        tasbeh = viewModel.tasbeh.value
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tasbeh") },
                navigationIcon = {
                    IconButton(onClick = { controller.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Orqaga")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.refreshTasbehAndAllTasbeh()
                        allTasbeh = 0
                        tasbeh = 0
                    }) {
                        Icon(Icons.Default.Refresh, "Tiklash")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Total Counter Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Jami",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = allTasbeh.toString(),
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = 56.sp
                    )
                }
            }
            
            // Main Counter Button
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    onClick = {
                        scale = 0.9f
                        if (tasbeh == 33) {
                            tasbeh = 0
                            viewModel.saveTasbeh(0)
                        } else {
                            tasbeh++
                            viewModel.saveTasbeh(tasbeh)
                        }
                        allTasbeh++
                        viewModel.saveAllTasbeh(allTasbeh)
                        scale = 1f
                    },
                    modifier = Modifier
                        .size(240.dp)
                        .scale(scaleAnimation),
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 8.dp,
                        pressedElevation = 16.dp
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tasbeh.toString(),
                            style = MaterialTheme.typography.displayLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 72.sp
                        )
                    }
                }
            }
            
            // Progress Indicator
            Column(
                modifier = Modifier.padding(bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LinearProgressIndicator(
                    progress = { tasbeh / 33f },
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(8.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "$tasbeh / 33",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
    
    LifecycleResumeEffect(Unit) {
        onPauseOrDispose {
            viewModel.saveInt(TASBEH, tasbeh)
            viewModel.saveInt(ALL_TASBEH, allTasbeh)
        }
    }
    
    BackHandler {
        viewModel.saveInt(TASBEH, tasbeh)
        viewModel.saveInt(ALL_TASBEH, allTasbeh)
        controller.popBackStack()
    }
}
