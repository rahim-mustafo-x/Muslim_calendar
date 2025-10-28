package uz.coder.muslimcalendar.presentation.screen

import android.icu.util.Calendar
import android.util.Log
import androidx.activity.compose.BackHandler
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import uz.coder.muslimcalendar.R
import uz.coder.muslimcalendar.domain.model.MuslimCalendar
import uz.coder.muslimcalendar.domain.model.Notification
import uz.coder.muslimcalendar.todo.MONTH
import uz.coder.muslimcalendar.presentation.ui.theme.Light_Blue
import uz.coder.muslimcalendar.presentation.ui.view.CalendarTopBar
import uz.coder.muslimcalendar.presentation.viewModel.NotificationViewModel
import uz.coder.muslimcalendar.presentation.viewModel.state.NotificationState

/* ---------- SCREEN ---------- */

@Composable
fun NotificationScreen(
    controller: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: NotificationViewModel = viewModel()
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.loadNotifications(
            listOf(
                context.getString(R.string.bomdod),
                context.getString(R.string.quyoshChiqishi),
                context.getString(R.string.peshin),
                context.getString(R.string.asr),
                context.getString(R.string.shom),
                context.getString(R.string.xufton)
            )
        )
    }
    var data by remember { mutableStateOf<List<MuslimCalendar>>(emptyList()) }
    var items by remember { mutableStateOf<List<Notification>>(emptyList()) }
    var listOfTimes by remember { mutableStateOf<List<String>>(emptyList()) }
    viewModel.setAlarm()
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { CalendarTopBar(list = emptyList()) {} }
    ) { padding ->
        Column(modifier
            .fillMaxSize()
            .padding(padding)) {
            CalendarTime(data = data){
                listOfTimes = it
            }
            HorizontalDivider()
            NotificationItems(
                list = items,
                listOfTimes = listOfTimes,
                onIconPick = { index, resId -> viewModel.updateIcon(index, resId) },
                modifier = Modifier
                    .fillMaxSize()
            )
        }
    }
    LaunchedEffect(viewModel.state) {
        viewModel.state.collect {
            when(it){
                NotificationState.Init -> {}
                NotificationState.Loading -> {}
                is NotificationState.Success -> {
                    data = it.data
                    items = it.list
                }
            }
        }
    }
    BackHandler { controller.popBackStack() }
}

@Composable
fun CalendarTime(modifier: Modifier = Modifier, data: List<MuslimCalendar>, onChange:(List<String>)-> Unit) {
    val calendar = Calendar.getInstance()
    var today by remember { mutableIntStateOf(calendar.get(Calendar.DAY_OF_MONTH)) }
    val date = data.find { it.day == today }?: MuslimCalendar()
    Log.d(TAG, "CalendarTime: $today")
    onChange(listOf(
        date.tongSaharlik,
        date.sunRise,
        date.peshin,
        date.asr,
        date.shomIftor,
        date.hufton
    ))
    if (date!= MuslimCalendar()){
        Row(modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(Light_Blue), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = {
                if(today>1) today--
                onChange(listOf(
                    date.tongSaharlik,
                    date.sunRise,
                    date.peshin,
                    date.asr,
                    date.shomIftor,
                    date.hufton
                ))
            }, modifier = modifier.weight(1f).padding(start = 10.dp)) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, null, tint = White)
            }

            Column(modifier
                .weight(10f)
                .fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("${date.weekday}, ${date.day} - ${MONTH[date.month-1]}", color = White, textAlign = TextAlign.End)
                Text("${date.hijriDay} ${date.hijriMonth}", color = White, textAlign = TextAlign.End)
            }
            IconButton(onClick = {
                if(today < data.size) today++
                onChange(listOf(
                    date.tongSaharlik,
                    date.sunRise,
                    date.peshin,
                    date.asr,
                    date.shomIftor,
                    date.hufton
                ))
            }, modifier = modifier.weight(1f).padding(end = 10.dp)){
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = White)
            }
        }
    }
}

/* ---------- ITEMS COLUMN ---------- */

@Composable
private fun NotificationItems(
    list: List<Notification>,
    listOfTimes: List<String>,
    onIconPick: (index: Int, resId: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        list.forEachIndexed { index, item ->
            NotificationItem(
                index = index,
                item = item,
                time = listOfTimes[index],
                onIconPick = onIconPick
            )
        }
    }
}

/* ---------- SINGLE ITEM ---------- */

@Composable
private fun NotificationItem(
    index: Int,
    item: Notification,
    time: String,
    onIconPick: (index: Int, resId: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    /*  Pair<iconRes, title>  */
    val iconOptions = listOf(
        R.drawable.ic_speaker_on   to stringResource(R.string.speaker_on),
        R.drawable.ic_speaker_cross to stringResource(R.string.speaker_off),
        R.drawable.ic_bell          to stringResource(R.string.bell)
    )

    var menuExpanded by remember { mutableStateOf(false) }

    Column {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            /* — nom — */
            Text(
                text = item.name,
                fontSize = 20.sp,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = time,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                textAlign = TextAlign.End
            )



            /* — asosiy icon + menyu — */
            Box {
                IconButton(onClick = { menuExpanded = !menuExpanded }) {
                    Icon(
                        painter = painterResource(id = item.icon),
                        contentDescription = null,
                        tint = Light_Blue
                    )
                }

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    iconOptions.forEach { (iconRes, title) ->
                        DropdownMenuItem(
                            text = { Text(title) },
                            onClick = {
                                menuExpanded = false
                                onIconPick(index, iconRes)   // tartib: index, resId
                            },
                            trailingIcon = {
                                Icon(
                                    painter = painterResource(id = iconRes),
                                    contentDescription = null,
                                    tint = Light_Blue
                                )
                            }
                        )
                    }
                }
            }
        }
        HorizontalDivider(Modifier.fillMaxWidth())
    }
}
private const val TAG = "NotificationScreen"