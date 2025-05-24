package uz.coder.muslimcalendar.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import uz.coder.muslimcalendar.R
import uz.coder.muslimcalendar.models.model.Notification
import uz.coder.muslimcalendar.ui.theme.Light_Blue
import uz.coder.muslimcalendar.ui.view.CalendarTopBar
import uz.coder.muslimcalendar.viewModel.NotificationViewModel
import uz.coder.muslimcalendar.viewModel.state.NotificationState

/* ---------- SCREEN ---------- */

@Composable
fun NotificationScreen(
    controller: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: NotificationViewModel = viewModel()
) {
    val context = LocalContext.current    /* --- namoz nomlarini faqat bir marta yuklaymiz --- */
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

    val uiState by viewModel.state.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { CalendarTopBar(list = emptyList()) {} }
    ) { padding ->
        when (uiState) {
            NotificationState.Init,
            NotificationState.Loading -> {
                // loading chiqarmoqchi bo‘lsangiz shu yerga qo‘ying
            }
            is NotificationState.Success -> {
                val items = (uiState as NotificationState.Success).list
                NotificationItems(
                    list = items,
                    onIconPick = { index, resId -> viewModel.updateIcon(index, resId) },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                )
            }
        }
    }

    BackHandler { controller.popBackStack() }
}

/* ---------- ITEMS COLUMN ---------- */

@Composable
private fun NotificationItems(
    list: List<Notification>,
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