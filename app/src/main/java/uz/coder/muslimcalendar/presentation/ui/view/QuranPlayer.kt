package uz.coder.muslimcalendar.presentation.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.delay
import uz.coder.muslimcalendar.R
import uz.coder.muslimcalendar.todo.formatTime
import uz.coder.muslimcalendar.presentation.ui.theme.Light_Blue

@Composable
fun QuranPlayer(
    exoPlayer: ExoPlayer,
    isPlaying: Boolean,
    onPlayPauseClick: () -> Unit,
    onNextClick: () -> Unit,
    onPreviousClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var sliderPosition by remember { mutableFloatStateOf(0f) }
    var duration by remember { mutableLongStateOf(1L) }
    var currentPosition by remember { mutableLongStateOf(0L) }
    var isCompleted by remember { mutableStateOf(false) }

    // 🔄 Update position & completion status
    LaunchedEffect(exoPlayer) {
        while (true) {
            currentPosition = exoPlayer.currentPosition
            duration = exoPlayer.duration.coerceAtLeast(1L)
            isCompleted = duration > 0 && currentPosition >= duration
            sliderPosition = currentPosition.toFloat() / duration.toFloat()
            delay(500)
        }
    }

    Surface(
        color = Light_Blue,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 🎚️ Slider
            Slider(
                value = sliderPosition,
                onValueChange = {
                    sliderPosition = it
                    exoPlayer.seekTo((duration * it).toLong())
                },
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = Color.White,
                    inactiveTrackColor = Color.Gray
                ),
                modifier = Modifier.fillMaxWidth()
            )

            // ⏱ Time display
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = currentPosition.formatTime(),
                    color = Color.White,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Start
                )
                Text(
                    text = duration.formatTime(),
                    color = Color.White,
                    fontSize = 12.sp,
                    textAlign = TextAlign.End
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 🎮 Controls
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)
            ) {
                IconButton(onClick = { onPreviousClick() }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_previous),
                        contentDescription = "Previous",
                        tint = Color.White
                    )
                }

                IconButton(onClick = {
                    if (isCompleted) {
                        exoPlayer.seekTo(0)
                        exoPlayer.playWhenReady = true
                        isCompleted = false
                    } else {
                        onPlayPauseClick()
                    }
                }) {
                    Icon(
                        painter = if (isPlaying && !isCompleted)
                            painterResource(R.drawable.ic_pause)
                        else
                            painterResource(R.drawable.ic_play),
                        contentDescription = "Play/Pause",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                IconButton(onClick = { onNextClick() }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_next),
                        contentDescription = "Next",
                        tint = Color.White
                    )
                }
            }
        }
    }
}
