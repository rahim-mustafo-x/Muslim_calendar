package uz.coder.muslimcalendar.screen

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import uz.coder.muslimcalendar.R
import uz.coder.muslimcalendar.models.model.SpinnerModel
import uz.coder.muslimcalendar.todo.ENG
import uz.coder.muslimcalendar.todo.RUS
import uz.coder.muslimcalendar.todo.UZB
import uz.coder.muslimcalendar.todo.appLanguage
import uz.coder.muslimcalendar.ui.view.Spinner
import uz.coder.muslimcalendar.viewModel.SettingsViewModel

@Composable
fun SettingsScreen(modifier: Modifier = Modifier, controller: NavHostController) {
    val viewModel = viewModel<SettingsViewModel>()
    val context = LocalContext.current
    val list = listOf(
        SpinnerModel(
            context.getString(R.string.unselected),
            ""
        ),
        SpinnerModel(
            context.getString(R.string.uzbek),
            UZB
        ),
        SpinnerModel(
            context.getString(R.string.russia),
            RUS
        ),
        SpinnerModel(
            context.getString(R.string.english),
            ENG
        )
    )
    var value by remember { mutableStateOf(list[0].spinnerValue) }
    LifecycleStartEffect(appLanguage) {
        onStopOrDispose {
            value = list.find { it.sign == appLanguage }?.spinnerValue
        }
    }
    Scaffold {
        Column(modifier = modifier
            .fillMaxSize()
            .padding(it)) {
            Spinner(modifier = modifier, value, list = list) {
                if (!it.sign.isBlank()){
                    value = it.spinnerValue
                    Log.d(TAG, "SettingsScreen: $it")
//                    viewModel.selectLanguage(it.sign)
                }else{
                    Toast.makeText(context, context.getString(R.string.select_language), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    BackHandler {
        controller.popBackStack()
    }
}
private const val TAG = "SettingsScreen"