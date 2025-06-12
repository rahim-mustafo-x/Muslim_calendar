package uz.coder.muslimcalendar.screen

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import uz.coder.muslimcalendar.R
import uz.coder.muslimcalendar.models.model.quran.Sura
import uz.coder.muslimcalendar.models.sealed.Screen
import uz.coder.muslimcalendar.ui.theme.Light_Blue
import uz.coder.muslimcalendar.ui.view.CalendarTopBar
import uz.coder.muslimcalendar.ui.view.SuraItem
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
    var searchText by remember { mutableStateOf("") }
    val context = LocalContext.current
    Scaffold(topBar = {CalendarTopBar(modifier = modifier, text = stringResource(R.string.quran), list =  emptyList()) {} },
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
            Column(
                modifier = modifier.padding(padding).fillMaxSize()
            ){
                OutlinedTextField(searchText, onValueChange = { searchText = it }, placeholder = {
                    Text(
                        text = stringResource(R.string.searchSura),
                        color = Light_Blue
                    )
                }, modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 5.dp), colors = TextFieldDefaults.colors(focusedTextColor = Light_Blue, unfocusedTextColor = Light_Blue, disabledTextColor = Light_Blue, focusedIndicatorColor = Light_Blue, unfocusedIndicatorColor = Light_Blue, focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent),
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = Light_Blue)
                    })
                LazyColumn(
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
    }
    LaunchedEffect(searchText) {
        suraList = viewModel.searchSura(searchText, suraList)
    }
    LaunchedEffect(viewModel.state) {
        viewModel.state.collect {
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
    BackHandler {
        controller.popBackStack()
    }
}
