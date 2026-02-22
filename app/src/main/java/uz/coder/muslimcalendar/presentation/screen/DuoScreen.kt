package uz.coder.muslimcalendar.presentation.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import uz.coder.muslimcalendar.domain.model.sealed.Screen
import uz.coder.muslimcalendar.presentation.ui.view.ModernListItem
import uz.coder.muslimcalendar.todo.dualist

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DuoScreen(modifier: Modifier = Modifier, controller: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Duolar") },
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
        }
    ) { padding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            itemsIndexed(dualist) { index, item ->
                ModernListItem(
                    title = item.name,
                    onClick = { controller.navigate(Screen.DuoMeaning.route + "/$index") }
                )
            }
        }
    }
    
    BackHandler {
        controller.popBackStack()
    }
}
