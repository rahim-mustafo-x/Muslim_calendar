package uz.coder.muslimcalendar.presentation.screen

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import uz.coder.muslimcalendar.domain.model.AzanSound
import uz.coder.muslimcalendar.presentation.viewModel.AdvancedSettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvancedSettingsScreen(
    controller: NavHostController,
    viewModel: AdvancedSettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val adjustments by viewModel.prayerAdjustments.collectAsState()
    
    var showAdjustmentDialog by remember { mutableStateOf(false) }
    var showAzanSoundDialog by remember { mutableStateOf(false) }
    var selectedPrayer by remember { mutableStateOf("") }
    
    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let {
            scope.launch {
                val json = viewModel.exportSettings()
                context.contentResolver.openOutputStream(it)?.use { output ->
                    output.write(json.toByteArray())
                }
                Toast.makeText(context, "Sozlamalar saqlandi", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            scope.launch {
                context.contentResolver.openInputStream(it)?.use { input ->
                    val json = input.bufferedReader().readText()
                    val success = viewModel.importSettings(json)
                    Toast.makeText(
                        context,
                        if (success) "Sozlamalar yuklandi" else "Xatolik yuz berdi",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Qo'shimcha sozlamalar") },
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
            // Prayer Time Adjustments
            SettingsSection(title = "Namoz vaqtlarini sozlash") {
                SettingsItem(
                    icon = Icons.Default.Schedule,
                    title = "Vaqtlarni o'zgartirish",
                    subtitle = "Har bir namoz vaqtini sozlang",
                    onClick = { showAdjustmentDialog = true }
                )
            }
            
            // Azan Sounds
            SettingsSection(title = "Azon ovozlari") {
                listOf("Bomdod", "Peshin", "Asr", "Shom", "Xufton").forEach { prayer ->
                    SettingsItem(
                        icon = Icons.AutoMirrored.Filled.VolumeUp,
                        title = "$prayer azoni",
                        subtitle = "Azon ovozini tanlang",
                        onClick = {
                            selectedPrayer = prayer.lowercase()
                            showAzanSoundDialog = true
                        }
                    )
                }
            }
            
            // Backup & Restore
            SettingsSection(title = "Zaxira nusxa") {
                SettingsItem(
                    icon = Icons.Default.CloudUpload,
                    title = "Sozlamalarni saqlash",
                    subtitle = "Barcha sozlamalarni eksport qilish",
                    onClick = { exportLauncher.launch("muslim_calendar_backup.json") }
                )
                
                SettingsItem(
                    icon = Icons.Default.CloudDownload,
                    title = "Sozlamalarni yuklash",
                    subtitle = "Avval saqlangan sozlamalarni tiklash",
                    onClick = { importLauncher.launch("application/json") }
                )
                
                SettingsItem(
                    icon = Icons.Default.RestartAlt,
                    title = "Sozlamalarni tiklash",
                    subtitle = "Barcha sozlamalarni boshlang'ich holatga qaytarish",
                    onClick = {
                        scope.launch {
                            viewModel.resetAllSettings()
                            Toast.makeText(context, "Sozlamalar tiklandi", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }
        }
    }
    
    if (showAdjustmentDialog) {
        PrayerAdjustmentDialog(
            adjustments = adjustments,
            onDismiss = { showAdjustmentDialog = !showAdjustmentDialog },
            onSave = { newAdjustments ->
                viewModel.savePrayerAdjustments(newAdjustments)
                showAdjustmentDialog = !showAdjustmentDialog
            }
        )
    }
    
    if (showAzanSoundDialog) {
        AzanSoundDialog(
            prayerName = selectedPrayer,
            onDismiss = { showAzanSoundDialog = !showAzanSoundDialog },
            onSelect = { sound ->
                viewModel.setAzanSound(selectedPrayer, sound)
                showAzanSoundDialog = !showAzanSoundDialog
            }
        )
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                content()
            }
        }
    }
}

@Composable
fun PrayerAdjustmentDialog(
    adjustments: uz.coder.muslimcalendar.domain.model.PrayerAdjustment,
    onDismiss: () -> Unit,
    onSave: (uz.coder.muslimcalendar.domain.model.PrayerAdjustment) -> Unit
) {
    var bomdod by remember { mutableIntStateOf(adjustments.bomdod) }
    var quyosh by remember { mutableIntStateOf(adjustments.quyosh) }
    var peshin by remember { mutableIntStateOf(adjustments.peshin) }
    var asr by remember { mutableIntStateOf(adjustments.asr) }
    var shom by remember { mutableIntStateOf(adjustments.shom) }
    var xufton by remember { mutableIntStateOf(adjustments.xufton) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Vaqtlarni sozlash") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text("Har bir namoz vaqtiga daqiqalarda qo'shish yoki ayirish", 
                    style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(16.dp))
                
                AdjustmentSlider("Bomdod", bomdod) { bomdod = it }
                AdjustmentSlider("Quyosh", quyosh) { quyosh = it }
                AdjustmentSlider("Peshin", peshin) { peshin = it }
                AdjustmentSlider("Asr", asr) { asr = it }
                AdjustmentSlider("Shom", shom) { shom = it }
                AdjustmentSlider("Xufton", xufton) { xufton = it }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onSave(uz.coder.muslimcalendar.domain.model.PrayerAdjustment(
                    bomdod, quyosh, peshin, asr, shom, xufton
                ))
            }) {
                Text("Saqlash")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Bekor qilish")
            }
        }
    )
}

@Composable
fun AdjustmentSlider(
    name: String,
    value: Int,
    onValueChange: (Int) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(name, style = MaterialTheme.typography.bodyMedium)
            Text("${if (value > 0) "+" else ""}$value daqiqa", 
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary)
        }
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = -30f..30f,
            steps = 59
        )
    }
}

@Composable
fun AzanSoundDialog(
    prayerName: String,
    onDismiss: () -> Unit,
    onSelect: (AzanSound) -> Unit
) {
    Log.d("TAG", "AzanSoundDialog: $prayerName")
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Azon ovozini tanlang") },
        text = {
            Column {
                AzanSound.entries.forEach { sound ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(sound) }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (sound == AzanSound.NOTIFICATION) 
                                Icons.Default.Notifications 
                            else
                                Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(sound.displayName)
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
