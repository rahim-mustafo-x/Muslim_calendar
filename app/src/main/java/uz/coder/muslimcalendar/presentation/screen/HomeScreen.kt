package uz.coder.muslimcalendar.presentation.screen

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.LifecycleEventObserver
import uz.coder.muslimcalendar.R
import uz.coder.muslimcalendar.presentation.viewModel.HomeViewModel
import uz.coder.muslimcalendar.shared.domain.model.MuslimCalendar
import uz.coder.muslimcalendar.todo.hijriMonthTranslations
import java.time.LocalDate
import java.time.LocalTime
import java.time.chrono.HijrahDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit
import java.util.Locale

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigate: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()
    val prayerTimes = state.prayerTimes
    val currentTime = LocalTime.now()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) viewModel.reloadSavedLocation()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(state.isLocationConfigured) {
        if (!state.isLocationConfigured) onNavigate("location_settings")
    }

    // Dynamic theme colors from MaterialTheme (supports system Light and Dark mode)
    val backgroundColor = MaterialTheme.colorScheme.background
    val contentColor = MaterialTheme.colorScheme.onBackground
    val primaryColor = MaterialTheme.colorScheme.primary

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        if (state.isLoading) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                color = primaryColor
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item {
                HomeHeader(onNavigate, contentColor)
            }
            
            item {
                DateAndLocationSection(
                    contentColor = contentColor,
                    locationName = state.locationName,
                    onLocationClick = { onNavigate("location_settings") }
                )
            }

            item {
                val prayers = listOf(
                    Triple("Bomdod", prayerTimes.tongSaharlik, 0),
                    Triple("Quyosh", prayerTimes.sunRise, 1),
                    Triple("Peshin", prayerTimes.peshin, 2),
                    Triple("Asr", prayerTimes.asr, 3),
                    Triple("Shom", prayerTimes.shomIftor, 4),
                    Triple("Xufton", prayerTimes.hufton, 5)
                )

                var nextPrayerIndex = -1
                val formatter = DateTimeFormatter.ofPattern("HH:mm")
                
                for (i in prayers.indices) {
                    try {
                        if (prayers[i].second.isEmpty()) continue
                        val prayerTime = LocalTime.parse(prayers[i].second, formatter)
                        if (currentTime.isBefore(prayerTime)) {
                            nextPrayerIndex = i
                            break
                        }
                    } catch (e: Exception) {}
                }
                
                // If all prayers today have passed, the next one is tomorrow's Bomdod
                val nextPrayer = if (nextPrayerIndex != -1) prayers[nextPrayerIndex] else prayers[0]
                val remainingTime = calculateRemainingTime(nextPrayer.second, currentTime, formatter)

                NextPrayerCard(
                    name = nextPrayer.first,
                    time = nextPrayer.second,
                    remaining = remainingTime,
                    primaryColor = primaryColor,
                    contentColor = contentColor
                )
            }

            item {
                PrayerTimeRow(prayerTimes, currentTime, primaryColor, contentColor)
            }

            item {
                NavigationList(onNavigate, contentColor, primaryColor)
            }
        }

        state.calendarStatus?.let { status ->
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(status, modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onSecondaryContainer)
                    TextButton(onClick = viewModel::requestCalendarDownload) { Text("Yangilash") }
                }
            }
        }
    }

    if (state.showCalendarDownloadPrompt) {
        AlertDialog(
            onDismissRequest = viewModel::declineCalendarDownload,
            title = { Text("Namoz vaqtlarini yangilash") },
            text = { Text("Aniq vaqtlar va eslatmalar uchun joriy hamda keyingi oy jadvali internet orqali telefonga saqlanadi. Keyin ular internetsiz ham ishlaydi.") },
            confirmButton = { TextButton(onClick = viewModel::approveCalendarDownload) { Text("Yuklab olish") } },
            dismissButton = { TextButton(onClick = viewModel::declineCalendarDownload) { Text("Hozir emas") } }
        )
    }
}

@Composable
fun HomeHeader(onNavigate: (String) -> Unit, contentColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column {
            Text(
                "Muslim Taqvim",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
            Text(
                "Allohga yaqinroq bo'lish uchun",
                style = MaterialTheme.typography.bodyMedium,
                color = contentColor.copy(alpha = 0.6f)
            )
        }
        IconButton(
            onClick = { onNavigate("settings") },
            modifier = Modifier.size(24.dp)
        ) {
            Icon(Icons.Default.Settings, contentDescription = "Settings", tint = contentColor)
        }
    }
}

@Composable
fun DateAndLocationSection(
    contentColor: Color,
    locationName: String,
    onLocationClick: () -> Unit
) {
    val today = LocalDate.now()
    val hijriDate = HijrahDate.now()
    val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.forLanguageTag("uz"))
    
    // Fallback translation strategy mapping HijrahDate variants robustly
    val hijriStr = hijriDate.toString() // e.g. "Hijrah-umalqura AH 1447-09-05"
    val parts = hijriStr.split(" ")
    val datePart = parts.lastOrNull() ?: ""
    val dateParts = datePart.split("-")
    
    val hijriYear = dateParts.getOrNull(0) ?: hijriDate.get(ChronoField.YEAR).toString()
    val monthNumber = dateParts.getOrNull(1)?.toIntOrNull() ?: hijriDate.get(ChronoField.MONTH_OF_YEAR)
    val hijriDay = dateParts.getOrNull(2)?.toIntOrNull() ?: hijriDate.get(ChronoField.DAY_OF_MONTH)

    val hijriMonth = when (monthNumber) {
        1 -> "Muharram"
        2 -> "Safar"
        3 -> "Rabiul avval"
        4 -> "Rabius sani"
        5 -> "Jumadil avval"
        6 -> "Jumadis sani"
        7 -> "Rajab"
        8 -> "Sha’bon"
        9 -> "Ramazon"
        10 -> "Shawvol"
        11 -> "Zulqa’da"
        12 -> "Zulhijja"
        else -> "Muharram"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Text(
            today.format(formatter),
            style = MaterialTheme.typography.titleMedium,
            color = contentColor
        )
        Text(
            "$hijriDay $hijriMonth $hijriYear",
            style = MaterialTheme.typography.bodyMedium,
            color = contentColor.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f), shape = RoundedCornerShape(16.dp))
                .clickable(onClick = onLocationClick)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Mintaqa / Joylashuv",
                    style = MaterialTheme.typography.bodySmall,
                    color = contentColor.copy(alpha = 0.5f)
                )
                Text(
                    text = locationName.ifBlank { "Joylashuvni tanlang" },
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Joylashuvni o'zgartirish",
                modifier = Modifier.size(24.dp),
                tint = contentColor.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun NextPrayerCard(name: String, time: String, remaining: String, primaryColor: Color, contentColor: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .height(180.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = primaryColor.copy(alpha = 0.12f))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.mosque),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .fillMaxHeight(0.8f)
                    .padding(end = 8.dp),
                contentScale = ContentScale.Fit,
                alpha = 0.2f
            )
            
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .align(Alignment.TopStart)
            ) {
                Text(
                    "Keyingi namoz",
                    color = primaryColor,
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
                Text(
                    time,
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
                Text(
                    "$remaining qoldi",
                    style = MaterialTheme.typography.bodyMedium,
                    color = contentColor.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
fun PrayerTimeRow(prayerTimes: MuslimCalendar, currentTime: LocalTime, primaryColor: Color, contentColor: Color) {
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    val prayers = listOf(
        Triple("Bomdod", prayerTimes.tongSaharlik, 0),
        Triple("Quyosh", prayerTimes.sunRise, 1),
        Triple("Peshin", prayerTimes.peshin, 2),
        Triple("Asr", prayerTimes.asr, 3),
        Triple("Shom", prayerTimes.shomIftor, 4),
        Triple("Xufton", prayerTimes.hufton, 5)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        prayers.forEachIndexed { index, (name, time, _) ->
            val isCurrent = isCurrentPrayer(time, prayers.getOrNull(index + 1)?.second ?: "23:59", currentTime, formatter)
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    name,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isCurrent) primaryColor else contentColor.copy(alpha = 0.6f),
                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                )
                Text(
                    time.ifEmpty { "--:--" },
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isCurrent) primaryColor else contentColor
                )
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(if (isCurrent) primaryColor else contentColor.copy(alpha = 0.2f))
                )
            }
        }
    }
}

@Composable
fun NavigationList(onNavigate: (String) -> Unit, contentColor: Color, primaryColor: Color) {
    val items = listOf(
        NavItem("Bugungi taqvim", "Sana, oy, hafta kuni", R.drawable.calendar, "calendar"),
        NavItem("Tasbeh", "Zikrlarni sanab boring", R.drawable.ic_prayer_beads, "tasbeh"),
        NavItem("Zikrlar", "Kunlik zikr va duolar", R.drawable.book, "duo"),
        NavItem("Allohning 99 ismi", "Go'zal ismlar va ma'nolari", R.drawable.muslim_man, "allah_names")
    )

    Column(
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items.forEach { item ->
            NavigationCard(item, onNavigate, contentColor, primaryColor)
        }
    }
}

data class NavItem(val title: String, val subtitle: String, val icon: Int, val route: String)

@Composable
fun NavigationCard(item: NavItem, onNavigate: (String) -> Unit, contentColor: Color, primaryColor: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onNavigate(item.route) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(12.dp),
                color = primaryColor.copy(alpha = 0.12f)
            ) {
                Icon(
                    painter = painterResource(id = item.icon),
                    contentDescription = null,
                    modifier = Modifier.padding(12.dp),
                    tint = primaryColor
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    item.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
                Text(
                    item.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = contentColor.copy(alpha = 0.65f)
                )
            }
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = contentColor.copy(alpha = 0.4f)
            )
        }
    }
}

private fun calculateRemainingTime(prayerTime: String, now: LocalTime, formatter: DateTimeFormatter): String {
    return try {
        if (prayerTime.isEmpty()) return ""
        val target = LocalTime.parse(prayerTime, formatter)
        var diff = ChronoUnit.MINUTES.between(now, target)
        if (diff < 0) diff += 24 * 60
        
        val hours = diff / 60
        val minutes = diff % 60
        
        buildString {
            if (hours > 0) append("$hours soat ")
            append("$minutes daqiqa")
        }
    } catch (e: Exception) {
        ""
    }
}

private fun isCurrentPrayer(startTime: String, endTime: String, now: LocalTime, formatter: DateTimeFormatter): Boolean {
    return try {
        if (startTime.isEmpty()) return false
        val start = LocalTime.parse(startTime, formatter)
        val end = LocalTime.parse(endTime, formatter)
        
        if (start.isBefore(end)) {
            !now.isBefore(start) && now.isBefore(end)
        } else {
            !now.isBefore(start) || now.isBefore(end)
        }
    } catch (e: Exception) {
        false
    }
}
