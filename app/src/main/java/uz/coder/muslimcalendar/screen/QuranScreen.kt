package uz.coder.muslimcalendar.screen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import uz.coder.muslimcalendar.models.model.quran.Sura
import uz.coder.muslimcalendar.models.sealed.Screen
import uz.coder.muslimcalendar.R
import uz.coder.muslimcalendar.ui.view.SuraItem
import uz.coder.muslimcalendar.ui.view.CalendarTopBar
import uz.coder.muslimcalendar.viewModel.QuranViewModel
import uz.coder.muslimcalendar.viewModel.state.QuranState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranScreen(
    modifier: Modifier = Modifier,
    controller: NavHostController,
) {
    val viewModel= viewModel<QuranViewModel>()
    var suraList by remember { mutableStateOf<List<Sura>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    if (suraList.isEmpty()){
        viewModel.loadQuran()
        isLoading = true
    }
    else{
        isLoading = false
    }
    val context = LocalContext.current
    Scaffold(
        topBar = {CalendarTopBar(modifier = modifier, stringResource = R.string.quran, list =  emptyList()) {}
        },
        modifier = modifier.fillMaxSize()
    ) { padding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                contentPadding = padding,
                modifier = modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                items(suraList) { sura ->
                    SuraItem(sura = sura) {
                        controller.navigate(Screen.QuranAyah.route + "/${sura.number}")
                    }
                }
            }
        }
    }
    LaunchedEffect(viewModel.surahList) {
        viewModel.surahList.collect {
            when(it){
                is QuranState.Error -> {
                    isLoading = false
                    print(it.message)
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }
                QuranState.Init -> {
                    isLoading = false
                }
                QuranState.Loading -> {
                    isLoading = true
                }
                is QuranState.Success -> {
                    isLoading = false
                    suraList = it.data
                }
            }
        }
    }
}
