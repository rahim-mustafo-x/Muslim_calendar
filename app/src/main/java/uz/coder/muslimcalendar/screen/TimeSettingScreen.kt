package uz.coder.muslimcalendar.screen

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import uz.coder.muslimcalendar.R
import uz.coder.muslimcalendar.viewModel.CalendarViewModel

@Composable
fun TimeSettingScreen(modifier: Modifier = Modifier, controller: NavHostController) {
    TimeSetting(modifier, controller = controller)
}
@Composable
fun TimeSetting(modifier: Modifier = Modifier, controller: NavHostController) {
    val context = LocalContext.current
    val viewModel = viewModel<CalendarViewModel>()
    val list by viewModel.timeList().collectAsState(initial = emptyList())
    Column(modifier.fillMaxSize()) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            var hasPostPermission by remember {
                mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PermissionChecker.PERMISSION_GRANTED)
            }
            val state = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) {
                hasPostPermission = it
            }
            Column {
                if (!hasPostPermission){
                    OutlinedButton(onClick = { state.launch(Manifest.permission.POST_NOTIFICATIONS) }) {
                        Text(stringResource(R.string.requastPermission))
                    }
                }
            }
        }
    }
}