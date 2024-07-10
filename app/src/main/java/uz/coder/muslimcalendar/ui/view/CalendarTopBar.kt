package uz.coder.muslimcalendar.ui.view

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import uz.coder.muslimcalendar.R
import uz.coder.muslimcalendar.model.model.Menu
import uz.coder.muslimcalendar.model.model.MenuScreen
import uz.coder.muslimcalendar.ui.theme.Dark_Green

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarTopBar(modifier:Modifier = Modifier, onClick:(MenuScreen)->Unit){
    val context = LocalContext.current
    val menuList = listOf(Menu(R.drawable.refresh, context.getString(R.string.refresh), MenuScreen.Refresh), Menu(R.drawable.region, context.getString(R.string.chooseRegion), MenuScreen.ChangeRegion), Menu(R.drawable.about, context.getString(R.string.about), MenuScreen.About))
    TopAppBar(title = { Text(text = stringResource(R.string.app_name), fontSize = 20.sp, modifier = modifier, color = White) }, colors = TopAppBarDefaults.topAppBarColors(
        Dark_Green), actions = {
            var showMenu by remember {
                mutableStateOf(false)
            }
            IconButton(onClick = { showMenu = !showMenu }) {
                Icon(Icons.Default.Menu, null, tint = White)
            }
            DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                menuList.forEachIndexed { _, menu ->
                    DropdownMenuItem(text = { Text(menu.text, fontSize = 15.sp, color = Black) }, onClick = { onClick(menu.menu); showMenu = false }, leadingIcon = { Icon(
                        painterResource(menu.img),
                        contentDescription = null,
                        tint = Black
                    ) })
                }
            }
    })
}