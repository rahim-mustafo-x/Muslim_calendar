package uz.coder.muslimcalendar.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uz.coder.muslimcalendar.viewModel.CalendarViewModel

@Composable
fun CalendarView(
    modifier: Modifier = Modifier,
    viewModel: CalendarViewModel,
    paddingValues: PaddingValues
) {
    val list by viewModel.oneMonth().collectAsState(emptyList())
    LazyVerticalGrid(columns = GridCells.Fixed(7), modifier = modifier.fillMaxSize().padding(paddingValues)) {
        itemsIndexed(list){_, item->
            CalendarItem(text = item.text, color = item.color, backgroundColor = item.backgroundColor)
        }
    }
}

@Composable
private fun CalendarItem(modifier: Modifier = Modifier, text:String, color:Color, backgroundColor: Color) {
    HorizontalDivider()
        Row {
            VerticalDivider()
            Box(modifier = modifier
                .height(50.dp)
                .width(90.dp)
                .background(backgroundColor), contentAlignment = Alignment.Center){
            Text(text = text, color = color, fontSize = 10.sp)
        }
            VerticalDivider()
    }
    HorizontalDivider()
}
