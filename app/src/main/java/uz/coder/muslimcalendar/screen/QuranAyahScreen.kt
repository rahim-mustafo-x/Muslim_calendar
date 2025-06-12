package uz.coder.muslimcalendar.screen

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import uz.coder.muslimcalendar.R
import uz.coder.muslimcalendar.models.model.Menu
import uz.coder.muslimcalendar.models.model.MenuSetting
import uz.coder.muslimcalendar.models.model.quran.SurahList
import uz.coder.muslimcalendar.todo.NUMBER
import uz.coder.muslimcalendar.todo.toAyahList
import uz.coder.muslimcalendar.ui.theme.Light_Blue
import uz.coder.muslimcalendar.ui.view.AyahArabicSection
import uz.coder.muslimcalendar.ui.view.AyahTranslationSection
import uz.coder.muslimcalendar.ui.view.CalendarTopBar
import uz.coder.muslimcalendar.ui.view.QuranPlayer
import uz.coder.muslimcalendar.viewModel.SurahViewModel
import uz.coder.muslimcalendar.viewModel.state.SurahState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranAyahScreen(
    modifier: Modifier = Modifier,
    controller: NavHostController,
    navBackStackEntry: NavBackStackEntry,
) {
    val viewModel = viewModel<SurahViewModel>()
    val context = LocalContext.current
    val number = navBackStackEntry.arguments?.getInt(NUMBER) ?: 1
    var showTranslation by remember { mutableStateOf(false) }
    var ayahList by remember { mutableStateOf<List<SurahList>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var audioPath by remember { mutableStateOf("") }
    var nameOfSura by remember { mutableStateOf(context.getString(R.string.app_name)) }
    val progress by viewModel.downloadProgress.collectAsState()

    // 🎬 ExoPlayer
    val exoPlayer = remember { ExoPlayer.Builder(context).build().apply {
        val mediaItem = MediaItem.fromUri(audioPath)
        setMediaItem(mediaItem)
        prepare()
    } }
    var isPlaying by remember { mutableStateOf(false) }
    var sliderPosition by remember { mutableFloatStateOf(0f) }
    val duration = exoPlayer.duration.coerceAtLeast(1L)

    // Update ExoPlayer when audioPath changes
    LaunchedEffect(audioPath) {
        if (audioPath.isNotEmpty()) {
            exoPlayer.apply {
                setMediaItem(MediaItem.fromUri(audioPath))
                prepare()
                if (isPlaying) play()
            }
        }
    }

    Scaffold(topBar = {
        CalendarTopBar(text = nameOfSura, list = listOf(Menu(R.drawable.ic_download,
            MenuSetting.Download))) { screen ->
            when (screen.ordinal) {
                MenuSetting.Download.ordinal -> {
                    Log.d(TAG, "QuranAyahScreen: $ayahList")
                    viewModel.downloadSurah(ayahList, audioPath)
                }
                else -> {}
            }
        }
    }, bottomBar = {
        QuranPlayer(
            exoPlayer = exoPlayer,
            isPlaying = isPlaying,
            onPlayPauseClick = {
                if (exoPlayer.isPlaying) {
                    exoPlayer.pause()
                } else {
                    exoPlayer.play()
                }
                isPlaying = !isPlaying
            },
            onNextClick = {
                exoPlayer.seekTo((exoPlayer.currentPosition + 5000).coerceAtMost(exoPlayer.duration))
            },
            onPreviousClick = {
                exoPlayer.seekTo((exoPlayer.currentPosition - 5000).coerceAtLeast(0))
            }
        )
    }) {
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            if (progress in 1..99){
                DownloadDialog(progress=progress){}
            }
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(it)
                    .padding(horizontal = 16.dp)
                    .padding(top = 5.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item {
                    AyahArabicSection(ayahList)
                }
                item {
                    Text(
                        text = stringResource(R.string.translation),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .clickable {
                                showTranslation = !showTranslation
                            }
                    )
                    if (showTranslation) {
                        AyahTranslationSection(ayahList)
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }

    BackHandler {
        controller.popBackStack()
    }

    LaunchedEffect(exoPlayer) {
        while (true) {
            sliderPosition = if (duration != 0L) exoPlayer.currentPosition.toFloat() / duration else 0f
            delay(500)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    LaunchedEffect(number) {
        viewModel.getSura(number)
        viewModel.getAudioPath(number.toString())
        Log.d(TAG, "QuranAyahScreen: $number")
    }

    LaunchedEffect(viewModel.getSura(number)) {
        viewModel.getNameOfSura(number).collect{
            nameOfSura = it.englishName
        }
    }

    LaunchedEffect(viewModel.state) {
        viewModel.state.collect { state ->
            when (state) {
                is SurahState.Loading -> isLoading = true
                is SurahState.Success -> {
                    Log.d(TAG, "QuranAyahScreen: ${state.data}")
                    isLoading = false
                    ayahList = state.data.toAyahList()
                }
                is SurahState.Error -> {
                    isLoading = true
                    Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                }
                SurahState.Init -> isLoading = false
            }
        }
    }


    LaunchedEffect(viewModel.audioPath) {
        viewModel.audioPath.collect {
            audioPath = it
            Log.d(TAG, "QuranAyahScreen: $it")
        }
    }
}

@Composable
fun DownloadDialog(
    modifier: Modifier = Modifier,
    progress: Int,
    onDismissRequest: () -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Box(
            modifier = modifier
                .padding(16.dp)
                .background(color = Light_Blue, shape = RoundedCornerShape(16.dp))
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.downloading) + "  $progress%",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                LinearProgressIndicator(
                    progress = progress / 100f,
                    modifier = modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = Color.Blue,
                    backgroundColor = Color.White
                )
            }
        }
    }
}

private const val TAG = "QuranAyahScreen"