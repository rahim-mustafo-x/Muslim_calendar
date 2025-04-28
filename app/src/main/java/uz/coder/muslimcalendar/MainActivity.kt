package uz.coder.muslimcalendar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import uz.coder.muslimcalendar.navigation.CalendarNavigation
import uz.coder.muslimcalendar.screen.allTasbeh
import uz.coder.muslimcalendar.screen.asr
import uz.coder.muslimcalendar.screen.bomdod
import uz.coder.muslimcalendar.screen.peshin
import uz.coder.muslimcalendar.screen.shom
import uz.coder.muslimcalendar.screen.tasbeh
import uz.coder.muslimcalendar.screen.vitr
import uz.coder.muslimcalendar.screen.xufton
import uz.coder.muslimcalendar.todo.ALL_TASBEH
import uz.coder.muslimcalendar.todo.ASR
import uz.coder.muslimcalendar.todo.BOMDOD
import uz.coder.muslimcalendar.todo.PESHIN
import uz.coder.muslimcalendar.todo.SHOM
import uz.coder.muslimcalendar.todo.TASBEH
import uz.coder.muslimcalendar.todo.VITR
import uz.coder.muslimcalendar.todo.XUFTON
import uz.coder.muslimcalendar.ui.theme.MuslimCalendarTheme
import uz.coder.muslimcalendar.viewModel.CalendarViewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MuslimCalendarTheme(darkTheme = false)  {
                    Greeting()
            }
        }
    }
}

@Composable
fun Greeting(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()){
        CalendarNavigation()
    }
}