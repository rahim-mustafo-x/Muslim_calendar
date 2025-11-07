package uz.coder.muslimcalendar.presentation.ui.view

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import uz.coder.muslimcalendar.R
import uz.coder.muslimcalendar.domain.model.Menu
import uz.coder.muslimcalendar.models.model.MenuSetting
import uz.coder.muslimcalendar.presentation.ui.theme.Light_Blue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarTopBar(modifier:Modifier = Modifier, text: String = stringResource(R.string.app_name), list: List<Menu>, onClick:(MenuSetting)->Unit){
    TopAppBar(title = { Text(text = text, fontSize = 20.sp, modifier = modifier, color = White) }, colors = TopAppBarDefaults.topAppBarColors(
        Light_Blue), actions = {
            var showMenu by remember {
                mutableStateOf(false)
            }
            if (list.size>1){
                IconButton(onClick = { showMenu = !showMenu }) {
                    Icon(Icons.Default.Menu, null, tint = White)
                }
                DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                    list.forEachIndexed { _, menu ->
                        DropdownMenuItem(text = { Text(menu.text, fontSize = 15.sp, color = Black) }, onClick = { onClick(menu.menu); showMenu = false }, leadingIcon = { Icon(
                            painterResource(menu.img),
                            contentDescription = null,
                            tint = Black
                        ) })
                    }
                }
            }else if(list.size == 1){
                list.forEachIndexed {_,item->
                    IconButton(onClick = { onClick(item.menu) }) {
                        Icon(painterResource(item.img), null, tint = White)
                    }
                }
            }
            else return@TopAppBar
    })
}