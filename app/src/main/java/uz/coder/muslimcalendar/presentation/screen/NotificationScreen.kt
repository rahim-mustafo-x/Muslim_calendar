package uz.coder.muslimcalendar.presentation.screen

import android.annotation.SuppressLint
import android.icu.util.Calendar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import uz.coder.muslimcalendar.R
import uz.coder.muslimcalendar.domain.model.MuslimCalendar
import uz.coder.muslimcalendar.domain.model.Notification
import uz.coder.muslimcalendar.presentation.ui.theme.Light_Blue
import uz.coder.muslimcalendar.presentation.ui.view.CalendarTopBar
import uz.coder.muslimcalendar.presentation.viewModel.NotificationViewModel
import uz.coder.muslimcalendar.presentation.viewModel.state.NotificationState

/* ================= SCREEN ================= */

@Composable
fun NotificationScreen(
    viewModel: NotificationViewModel = hiltViewModel()
) {
    val calendarData by viewModel.oneMonthDay().collectAsState(emptyList())
    val notifications by viewModel.notifications.collectAsState()

    var times by remember { mutableStateOf(List(6) { "--:--" }) }

    Scaffold(
        topBar = { CalendarTopBar(list = emptyList()) {} }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
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
/* ================= CALENDAR HEADER ================= */

@SuppressLint("LocalContextResourcesRead")
@Composable
fun CalendarTime(
    data: List<MuslimCalendar>,
    onChange: (List<String>) -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    var today by remember { mutableIntStateOf(calendar.get(Calendar.DAY_OF_MONTH)) }

    val date = data.firstOrNull { it.day == today }

    LaunchedEffect(today, data) {
        if (date != null) {
            onChange(
                listOf(
                    date.tongSaharlik ?: "--:--",
                    date.sunRise ?: "--:--",
                    date.peshin ?: "--:--",
                    date.asr ?: "--:--",
                    date.shomIftor ?: "--:--",
                    date.hufton ?: "--:--"
                )
            )
        }
    }

    if (date != null) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(Light_Blue),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                onClick = { if (today > 1) today-- },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, null, tint = White)
            }

            Text(
                text = "${date.weekday}, ${date.day} - ${context.resources.getStringArray(R.array.months)[date.month - 1]}",
                color = White,
                modifier = Modifier.weight(10f),
                textAlign = TextAlign.Center
            )

            IconButton(
                onClick = { if (today < data.size) today++ },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = White)
            }
        }
    }
}

/* ================= ITEMS ================= */

@Composable
private fun NotificationItems(
    list: List<Notification>,
    listOfTimes: List<String>,
    onIconPick: (Int, Int) -> Unit
) {
    Column {
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

/* ================= SINGLE ITEM ================= */

@Composable
private fun NotificationItem(
    index: Int,
    item: Notification,
    time: String,
    onIconPick: (Int, Int) -> Unit
) {
    val iconOptions = listOf(
        R.drawable.ic_speaker_on to stringResource(R.string.speaker_on),
        R.drawable.ic_speaker_cross to stringResource(R.string.speaker_off),
        R.drawable.ic_bell to stringResource(R.string.bell)
    )

    var expanded by remember { mutableStateOf(false) }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = item.name ?: "",
                fontSize = 18.sp,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = time,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.End
            )

            Box {
                IconButton(onClick = { expanded = true }) {
                    Icon(
                        painter = painterResource(item.icon),
                        contentDescription = null,
                        tint = Light_Blue
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    iconOptions.forEach { (icon, title) ->
                        DropdownMenuItem(
                            text = { Text(title) },
                            onClick = {
                                expanded = false
                                onIconPick(index, icon)
                            },
                            trailingIcon = {
                                Icon(
                                    painter = painterResource(icon),
                                    contentDescription = null,
                                    tint = Light_Blue
                                )
                            }
                        )
                    }
                }
            }
        }
        HorizontalDivider()
    }
}
