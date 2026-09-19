package uz.coder.muslimcalendar.presentation.screen

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.androidx.compose.koinViewModel
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import uz.coder.muslimcalendar.R
import uz.coder.muslimcalendar.domain.model.Menu
import uz.coder.muslimcalendar.domain.model.MenuSetting
import uz.coder.muslimcalendar.domain.model.quran.SurahList
import uz.coder.muslimcalendar.presentation.ui.view.CalendarTopBar
import uz.coder.muslimcalendar.presentation.ui.view.QuranPlayer
import uz.coder.muslimcalendar.presentation.viewModel.SurahViewModel
import uz.coder.muslimcalendar.presentation.viewModel.state.SurahState
import uz.coder.muslimcalendar.todo.NUMBER
import uz.coder.muslimcalendar.todo.toAyahList
import uz.coder.muslimcalendar.todo.toArabicNumbers

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranAyahScreen(
    modifier: Modifier = Modifier,
    controller: NavHostController,
    navBackStackEntry: NavBackStackEntry,
) {
    val viewModel = koinViewModel<SurahViewModel>()
    val context = LocalContext.current
    val number = navBackStackEntry.arguments?.getInt(NUMBER) ?: 1
    var ayahList by remember { mutableStateOf<List<SurahList>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var audioPath by remember { mutableStateOf("") }
    var nameOfSura by remember { mutableStateOf(context.getString(R.string.app_name)) }

    // ExoPlayer
    val exoPlayer = remember { ExoPlayer.Builder(context).build().apply {
        val mediaItem = MediaItem.fromUri(audioPath)
        setMediaItem(mediaItem)
        prepare()
    } }
    var isPlaying by remember { mutableStateOf(false) }

    LaunchedEffect(audioPath) {
        if (audioPath.isNotEmpty()) {
            exoPlayer.apply {
                setMediaItem(MediaItem.fromUri(audioPath))
                prepare()
                if (isPlaying) play()
            }
        }
    }

    Scaffold(
        topBar = {
            CalendarTopBar(
                text = nameOfSura,
                list = listOf(Menu(R.drawable.ic_download, MenuSetting.Download))
            ) { menu ->
                if (menu == MenuSetting.Download) {
                    viewModel.downloadSurah(ayahList, audioPath)
                }
            }
        },
        bottomBar = {
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
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFF1A1A1A)),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                            fontSize = 28.sp,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    }
                }

                items(ayahList) { ayah ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .background(Color(0xFF242424), shape = RoundedCornerShape(12.dp))
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "${ayah.arabicText} ﴿${ayah.aya}﴾".toArabicNumbers(),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFE0E0E0),
                            textAlign = TextAlign.End,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "${ayah.aya}. ${ayah.translation}",
                            fontSize = 16.sp,
                            color = Color(0xFFB0B0B0),
                            textAlign = TextAlign.Start,
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (ayah.footnotes.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "📌 ${ayah.footnotes}",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Start,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val formattedText = "Oyat: ${ayah.arabicText}\nMa'nosi: ${ayah.translation}"
                                val clip = ClipData.newPlainText("Ayah text", formattedText)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Nusxalandi", Toast.LENGTH_SHORT).show()
                            }) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Nusxalash",
                                    tint = Color(0xFF888888)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    BackHandler {
        controller.popBackStack()
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    LaunchedEffect(number) {
        viewModel.getSura(number)
        viewModel.getAudioPath(number.toString())
    }

    LaunchedEffect(viewModel.getSura(number)) {
        viewModel.getNameOfSura(number).collect {
            nameOfSura = it.englishName
        }
    }

    LaunchedEffect(viewModel.state) {
        viewModel.state.collect { state ->
            when (state) {
                is SurahState.Loading -> isLoading = true
                is SurahState.Success -> {
                    isLoading = false
                    ayahList = state.data.toAyahList()
                }
                is SurahState.Error -> {
                    isLoading = false
                    Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                }
                SurahState.Init -> isLoading = false
            }
        }
    }

    LaunchedEffect(viewModel.audioPath) {
        viewModel.audioPath.collect {
            audioPath = it
        }
    }
}
