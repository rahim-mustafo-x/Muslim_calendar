package uz.coder.muslimcalendar.presentation.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Info
import uz.coder.muslimcalendar.BuildConfig
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import uz.coder.muslimcalendar.presentation.ui.theme.ThemeMode
import uz.coder.muslimcalendar.presentation.viewModel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    controller: NavHostController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val themeMode by viewModel.themeMode.collectAsState()
    var showThemeDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sozlamalar") },
                navigationIcon = {
                    IconButton(onClick = { controller.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Orqaga")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Theme Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Ko'rinish",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    SettingsItem(
                        icon = Icons.Default.Palette,
                        title = "Mavzu",
                        subtitle = when (themeMode) {
                            ThemeMode.LIGHT -> "Yorug'"
                            ThemeMode.DARK -> "Qorong'i"
                            ThemeMode.SYSTEM -> "Tizim"
                        },
                        onClick = { showThemeDialog = true }
                    )
                }
            }

            // About Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Xususiyatlar",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    SettingsItem(
                        icon = Icons.Default.Tune,
                        title = "Qo'shimcha sozlamalar",
                        subtitle = "Namoz vaqtlari, azon ovozlari",
                        onClick = { controller.navigate(uz.coder.muslimcalendar.domain.model.sealed.Screen.AdvancedSettings.route) }
                    )
                    
                    SettingsItem(
                        icon = Icons.Default.Explore,
                        title = "Qibla yo'nalishi",
                        subtitle = "Kompas orqali qiblani aniqlash",
                        onClick = { controller.navigate(uz.coder.muslimcalendar.domain.model.sealed.Screen.QiblaCompass.route) }
                    )
                    
                    SettingsItem(
                        icon = Icons.Default.BarChart,
                        title = "Namoz statistikasi",
                        subtitle = "O'qilgan namozlar hisobi",
                        onClick = { controller.navigate(uz.coder.muslimcalendar.domain.model.sealed.Screen.PrayerStatistics.route) }
                    )
                }
            }
            
            // About Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Ilova haqida",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    SettingsItem(
                        icon = Icons.Default.Info,
                        title = "Versiya",
                        subtitle = BuildConfig.VERSION_NAME,
                        onClick = {}
                    )
                }
            }
        }
    }

    if (showThemeDialog) {
        ThemeSelectionDialog(
            currentTheme = themeMode,
            onThemeSelected = { 
                viewModel.setThemeMode(it)
                showThemeDialog = !showThemeDialog
            },
            onDismiss = { showThemeDialog = !showThemeDialog }
        )
    }
}

@Composable
fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ThemeSelectionDialog(
    currentTheme: ThemeMode,
    onThemeSelected: (ThemeMode) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Mavzuni tanlang") },
        text = {
            Column {
                ThemeMode.entries.forEach { mode ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onThemeSelected(mode) }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = currentTheme == mode,
                            onClick = { onThemeSelected(mode) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = when (mode) {
                                ThemeMode.LIGHT -> "Yorug'"
                                ThemeMode.DARK -> "Qorong'i"
                                ThemeMode.SYSTEM -> "Tizim"
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Yopish")
            }
        }
    )
}
