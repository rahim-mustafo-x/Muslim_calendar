package uz.coder.muslimcalendar.ui.view

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import uz.coder.muslimcalendar.R
import uz.coder.muslimcalendar.model.sealed.Screen
import uz.coder.muslimcalendar.ui.theme.Dark_Green
import uz.coder.muslimcalendar.viewModel.CalendarViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarTopBar(modifier:Modifier = Modifier, controller:NavHostController, viewModel: CalendarViewModel){
    TopAppBar(title = { Text(text = stringResource(R.string.app_name), fontSize = 20.sp, modifier = modifier, color = White) }, colors = TopAppBarDefaults.topAppBarColors(
        Dark_Green), actions = {
            var showMenu by remember {
                mutableStateOf(false)
            }
            IconButton(onClick = { showMenu = !showMenu }) {
                Icon(Icons.Default.Menu, null, tint = White)
            }
            DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                DropdownMenuItem(text = { Text(stringResource(R.string.refresh), fontSize = 20.sp) }, onClick = { viewModel.loading() ; showMenu = false })
                DropdownMenuItem(text = { Text(stringResource(R.string.chooseRegion), fontSize = 20.sp) }, onClick = { controller.navigate(
                    Screen.ChooseRegion.route) })
            }
    })
}