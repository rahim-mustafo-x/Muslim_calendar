package uz.coder.muslimcalendar.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import uz.coder.muslimcalendar.domain.model.sealed.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreMenuScreen(controller: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Yana") },
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
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    SettingsItem(
                        icon = Icons.Default.Settings,
                        title = "Sozlamalar",
                        subtitle = "Ilova sozlamalari va parametrlari",
                        onClick = { controller.navigate(Screen.Settings.route) }
                    )
                    
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    
                    SettingsItem(
                        icon = Icons.Default.Apps,
                        title = "Ko'proq ilovalar",
                        subtitle = "Tavsiya etilgan boshqa ilovalar",
                        onClick = { controller.navigate("more_apps") }
                    )
                }
            }
        }
    }
}
