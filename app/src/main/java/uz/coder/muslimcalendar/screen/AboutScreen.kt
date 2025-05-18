package uz.coder.muslimcalendar.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uz.coder.muslimcalendar.R
import uz.coder.muslimcalendar.ui.view.CalendarTopBar

@Composable
fun AboutScreen(modifier: Modifier = Modifier) {
    About(modifier)
}

@Composable
fun About(modifier: Modifier = Modifier) {
    val state = rememberScrollState()
    val context = LocalContext.current
    Scaffold(topBar = { CalendarTopBar(list = emptyList()) {} }){
        Box(modifier = modifier
            .fillMaxSize()
            .padding(it)){
            Text(
                context.getString(R.string.about_text),
                modifier.fillMaxSize().padding(horizontal = 5.dp).verticalScroll(state),
                fontSize = 25.sp,
                color = Black
            )
        }
    }
}
