package uz.coder.muslimcalendar.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import uz.coder.muslimcalendar.R
import uz.coder.muslimcalendar.ui.theme.Dark_Green

@Composable
fun AboutScreen(modifier: Modifier = Modifier) {
    About(modifier)
}

@Composable
fun About(modifier: Modifier = Modifier) {
    val state = rememberScrollState()
    val context = LocalContext.current
    Box(modifier = modifier.fillMaxSize().background(Dark_Green).verticalScroll(state)){
        Text(context.getString(R.string.about_text), modifier.fillMaxSize(), fontSize = 25.sp, color = White)
    }
}
