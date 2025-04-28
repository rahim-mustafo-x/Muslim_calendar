package uz.coder.muslimcalendar.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Icon
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.IconButton
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Slider
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.SliderDefaults
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Surface
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Text
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
import uz.coder.muslimcalendar.ui.theme.Light_Blue

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

    Surface(
        color = Light_Blue,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        modifier = modifier
            .fillMaxWidth()
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

            // 🕓 Current time / Total duration
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatTime(currentPosition),
                    color = Color.White,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Start
                )
                Text(
                    text = formatTime(duration),
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
                modifier = Modifier.fillMaxWidth()
            ) {

                IconButton(onClick = { onPreviousClick() }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_previous),
                        contentDescription = "Previous",
                        tint = Color.White)
                }

                IconButton(onClick = { onPlayPauseClick() }) {
                    Icon(
                        painter = if (isPlaying) painterResource(R.drawable.ic_pause) else painterResource(R.drawable.ic_play),
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

    // 🔄 Slider and Time
    LaunchedEffect(exoPlayer) {
        while (true) {
            currentPosition = exoPlayer.currentPosition
            val dur = exoPlayer.duration
            if (dur > 0) {
                duration = dur
            }
            delay(500)
        }
    }
}

fun formatTime(ms: Long): String {
    val totalSeconds = ms / 1000
    val hour = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) /60
    val seconds = totalSeconds % 60
    return "%02d:%02d:%02d".format(hour, minutes, seconds)
}

