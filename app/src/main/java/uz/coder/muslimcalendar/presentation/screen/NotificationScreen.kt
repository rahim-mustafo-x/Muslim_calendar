package uz.coder.muslimcalendar.presentation.screen

import android.annotation.SuppressLint
import android.icu.util.Calendar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import uz.coder.muslimcalendar.domain.model.MuslimCalendar
import uz.coder.muslimcalendar.domain.model.Notification
import uz.coder.muslimcalendar.presentation.viewModel.NotificationViewModel
import uz.coder.muslimcalendar.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    viewModel: NotificationViewModel = hiltViewModel()
) {
    val calendarData by viewModel.oneMonthDay().collectAsState(emptyList())
    val notifications by viewModel.notifications.collectAsState()
    var times by remember { mutableStateOf(List(6) { "--:--" }) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Eslatmalar") },
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
        ) {
            CalendarTime(calendarData) { times = it }
            HorizontalDivider()
            NotificationItems(
                list = notifications,
                listOfTimes = times,
                onIconPick = viewModel::updateIcon
            )
        }
    }
}

@SuppressLint("LocalContextResourcesRead")
@Composable
fun CalendarTime(
    data: List<MuslimCalendar>,
    onChange: (List<String>) -> Unit
) {
    val calendar = Calendar.getInstance()
    var today by remember { mutableIntStateOf(calendar.get(Calendar.DAY_OF_MONTH)) }
    val date = data.firstOrNull { it.day == today }

    LaunchedEffect(today, data) {
        if (date != null) {
            onChange(
                listOf(
                    date.tongSaharlik,
                    date.sunRise,
                    date.peshin,
                    date.asr,
                    date.shomIftor,
                    date.hufton
                )
            )
        }
    }

    if (date != null) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { if (today > 1) today-- }) {
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Text(
                    text = "${date.weekday}, ${date.day}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    textAlign = TextAlign.Center
                )

                IconButton(onClick = { if (today < data.size) today++ }) {
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationItems(
    list: List<Notification>,
    listOfTimes: List<String>,
    onIconPick: (Int, Int) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        list.forEachIndexed { index, item ->
            NotificationItem(
                index = index,
                item = item,
                time = listOfTimes.getOrElse(index) { "--:--" },
                onIconPick = onIconPick
            )
        }
    }
}

@Composable
private fun NotificationItem(
    index: Int,
    item: Notification,
    time: String,
    onIconPick: (Int, Int) -> Unit
) {
    val iconOptions = listOf(
        Triple(R.drawable.ic_speaker_on, "Azon", Icons.AutoMirrored.Filled.VolumeUp),
        Triple(R.drawable.ic_speaker_cross, "O'chirish", Icons.AutoMirrored.Filled.VolumeOff),
        Triple(R.drawable.ic_bell, "Bildirishnoma", Icons.Default.Notifications)
    )

    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = item.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = time,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.End
            )

            Box {
                IconButton(onClick = { expanded = true }) {
                    Icon(
                        imageVector = when (item.icon) {
                            R.drawable.ic_speaker_on -> Icons.AutoMirrored.Filled.VolumeUp
                            R.drawable.ic_speaker_cross -> Icons.AutoMirrored.Filled.VolumeOff
                            else -> Icons.Default.Notifications
                        },
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    iconOptions.forEach { (icon, title, materialIcon) ->
                        DropdownMenuItem(
                            text = { Text(title) },
                            onClick = {
                                expanded = false
                                onIconPick(index, icon)
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = materialIcon,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}
