package uz.coder.muslimcalendar.presentation.ui.view

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import uz.coder.muslimcalendar.domain.model.Menu
import uz.coder.muslimcalendar.domain.model.MenuSetting

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarTopBar(
    modifier: Modifier = Modifier,
    text: String = "Muslim Calendar",
    list: List<Menu>,
    onClick: (MenuSetting) -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = text,
                style = MaterialTheme.typography.titleLarge
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        actions = {
            var showMenu by remember { mutableStateOf(false) }
            
            if (list.size > 1) {
                IconButton(onClick = { showMenu = !showMenu }) {
                    Icon(Icons.Default.MoreVert, "Menu")
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    list.forEach { menu ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    menu.text,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            },
                            onClick = {
                                onClick(menu.menu)
                                showMenu = false
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = getMenuIcon(menu.menu),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        )
                    }
                }
            } else if (list.size == 1) {
                list.forEach { item ->
                    IconButton(onClick = { onClick(item.menu) }) {
                        Icon(
                            imageVector = getMenuIcon(item.menu),
                            contentDescription = item.text
                        )
                    }
                }
            }
        }
    )
}

private fun getMenuIcon(menuSetting: MenuSetting) = when (menuSetting) {
    MenuSetting.Notification -> Icons.Default.Notifications
    MenuSetting.About -> Icons.Default.Info
    MenuSetting.Settings -> Icons.Default.Settings
    MenuSetting.RefreshTasbeh -> Icons.Default.RestartAlt
    MenuSetting.Download -> Icons.Default.Download
    else -> Icons.Default.MoreVert
}
