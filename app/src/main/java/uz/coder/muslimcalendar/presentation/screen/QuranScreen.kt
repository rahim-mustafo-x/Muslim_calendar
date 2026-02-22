package uz.coder.muslimcalendar.presentation.screen

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import uz.coder.muslimcalendar.domain.model.quran.Sura
import uz.coder.muslimcalendar.domain.model.sealed.Screen
import uz.coder.muslimcalendar.presentation.ui.view.SuraItem
import uz.coder.muslimcalendar.presentation.viewModel.QuranViewModel
import uz.coder.muslimcalendar.presentation.viewModel.state.QuranState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranScreen(
    modifier: Modifier = Modifier,
    controller: NavHostController,
) {
    val viewModel = hiltViewModel<QuranViewModel>()
    var suraList by remember { mutableStateOf<List<Sura>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var searchText by remember { mutableStateOf("") }
    val context = LocalContext.current
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Qur'on") },
                navigationIcon = {
                    IconButton(onClick = { controller.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Orqaga")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        modifier = modifier.fillMaxSize()
    ) { padding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            Column(
                modifier = modifier
                    .padding(padding)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                // Search Bar
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    placeholder = { Text("Sura qidirish...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    ),
                    singleLine = true
                )
                
                // Sura List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
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
            when (it) {
                is QuranState.Error -> {
                    isLoading = false
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
