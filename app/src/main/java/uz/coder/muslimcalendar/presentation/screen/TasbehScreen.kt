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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import uz.coder.muslimcalendar.presentation.viewModel.TasbehViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasbehScreen(modifier: Modifier = Modifier, controller: NavHostController) {
    val viewModel: TasbehViewModel = hiltViewModel()
    val allTasbeh by viewModel.allTasbeh.collectAsStateWithLifecycle()
    val tasbeh by viewModel.tasbeh.collectAsStateWithLifecycle()
    
    var scale by remember { mutableFloatStateOf(1f) }
    
    val scaleAnimation by animateFloatAsState(
        targetValue = scale,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )
    
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
                    IconButton(onClick = { viewModel.reset() }) {
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
                        val newTasbeh = if (tasbeh == 33) 1 else tasbeh + 1
                        viewModel.saveTasbeh(newTasbeh)
                        viewModel.saveAllTasbeh(allTasbeh + 1)
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
    
    BackHandler {
        controller.popBackStack()
    }
}
