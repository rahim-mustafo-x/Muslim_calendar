package uz.coder.muslimcalendar.presentation.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import uz.coder.muslimcalendar.presentation.viewModel.CalendarViewModel

@Composable
fun CalendarView(
    modifier: Modifier = Modifier,
    viewModel: CalendarViewModel,
    paddingValues: PaddingValues
) {
    val list by viewModel.oneMonth().collectAsState(emptyList())
    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(8.dp)
    ) {
        itemsIndexed(list) { _, item ->
            CalendarItem(
                text = item.text,
                color = item.color,
                backgroundColor = item.backgroundColor
            )
        }
    }
}

@Composable
private fun CalendarItem(
    modifier: Modifier = Modifier,
    text: String,
    color: Color,
    backgroundColor: Color
) {
    Box(
        modifier = modifier
            .padding(2.dp)
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (backgroundColor == Color.Transparent) {
                    MaterialTheme.colorScheme.surface
                } else {
                    backgroundColor
                }
            )
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(8.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (color == Color.Black || color == Color.White) {
                MaterialTheme.colorScheme.onSurface
            } else {
                color
            },
            style = MaterialTheme.typography.bodySmall
        )
    }
}
