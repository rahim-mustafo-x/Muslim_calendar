package uz.coder.muslimcalendar.presentation.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import uz.coder.muslimcalendar.presentation.viewModel.CalendarViewModel

@Composable
fun CalendarView(
    modifier: Modifier = Modifier,
    viewModel: CalendarViewModel,
    paddingValues: PaddingValues
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        if (state.isLoading && state.calendarList.isEmpty()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                items(state.calendarList) { item ->
                    CalendarItem(
                        text = item.text,
                        color = item.color,
                        backgroundColor = item.backgroundColor
                    )
                }
            }
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
    val isHeader = backgroundColor != Color.White && backgroundColor != Color.Transparent
    
    Box(
        modifier = modifier
            .padding(1.dp)
            .aspectRatio(1f)
            .clip(RoundedCornerShape(4.dp))
            .background(
                if (backgroundColor == Color.Transparent || backgroundColor == Color.Unspecified) {
                    MaterialTheme.colorScheme.surface
                } else {
                    backgroundColor
                }
            )
            .border(
                width = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(4.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (color == Color.Unspecified) {
                MaterialTheme.colorScheme.onSurface
            } else if (color == Color.White && !isHeader) {
                MaterialTheme.colorScheme.onSurface
            } else {
                color
            },
            style = MaterialTheme.typography.bodySmall,
            fontSize = if (isHeader) 10.sp else 12.sp,
            fontWeight = if (isHeader) FontWeight.Bold else FontWeight.Normal,
            maxLines = 1
        )
    }
}
