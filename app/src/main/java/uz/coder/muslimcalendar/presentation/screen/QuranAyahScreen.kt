package uz.coder.muslimcalendar.presentation.screen

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import org.koin.androidx.compose.koinViewModel
import uz.coder.muslimcalendar.R
import uz.coder.muslimcalendar.domain.model.Menu
import uz.coder.muslimcalendar.domain.model.MenuSetting
import uz.coder.muslimcalendar.domain.model.quran.SurahList
import uz.coder.muslimcalendar.presentation.ui.view.CalendarTopBar
import uz.coder.muslimcalendar.presentation.ui.view.QuranPlayer
import uz.coder.muslimcalendar.presentation.viewModel.SurahViewModel
import uz.coder.muslimcalendar.presentation.viewModel.state.SurahState
import uz.coder.muslimcalendar.todo.NUMBER
import uz.coder.muslimcalendar.todo.toArabicNumbers
import uz.coder.muslimcalendar.todo.toAyahList

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@SuppressLint("LocalContextGetResourceValueCall")
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

    val isPlaying by viewModel.quranPlayerManager.isPlaying.collectAsState()
    val playbackPosition by viewModel.quranPlayerManager.playbackPosition.collectAsState()
    val duration by viewModel.quranPlayerManager.duration.collectAsState()
    val isAudioPreparing by viewModel.quranPlayerManager.isPreparing.collectAsState()

    val sliderPosition = if (duration > 0) playbackPosition.toFloat() / duration.toFloat() else 0f

    val isDark = isSystemInDarkTheme()
    val listBackgroundColor = MaterialTheme.colorScheme.background
    val cardBackgroundColor = if (isDark) Color(0xFF242424) else MaterialTheme.colorScheme.surfaceVariant
    val primaryTextColor = MaterialTheme.colorScheme.onBackground
    val secondaryTextColor = MaterialTheme.colorScheme.onSurfaceVariant

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
                isPlaying = isPlaying,
                isPreparing = isAudioPreparing,
                sliderPosition = sliderPosition,
                currentPosition = playbackPosition,
                duration = duration,
                onValueChange = {
                    viewModel.quranPlayerManager.seekTo((duration * it).toLong())
                },
                onPlayPauseClick = {
                    if (viewModel.quranPlayerManager.currentTrackTitle.value != nameOfSura) {
                        viewModel.quranPlayerManager.playSurah(audioPath, nameOfSura)
                    } else {
                        viewModel.quranPlayerManager.togglePlayPause()
                    }
                },
                onNextClick = {
                    viewModel.quranPlayerManager.fastForward()
                },
                onPreviousClick = {
                    viewModel.quranPlayerManager.rewind()
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
                    .background(listBackgroundColor),
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
                            text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                            fontSize = 28.sp,
                            color = primaryTextColor,
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
                            .background(cardBackgroundColor, shape = RoundedCornerShape(12.dp))
                            .padding(16.dp)
                    ) {
                        // Arabcha matn va oyat raqamini o'ngdan chapga (RTL) yo'naltirish
                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start,
                                verticalArrangement = Arrangement.Center
                            ) {
                                // Oyatning arabcha matni
                                Text(
                                    text = ayah.arabicText,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = primaryTextColor,
                                    textAlign = TextAlign.Start
                                )

                                Spacer(modifier = Modifier.width(6.dp))
                                // Oyat raqami (Har doim {x} ko'rinishida chiqadi)
                                Text(
                                    text = "\u200F{\u200F${ayah.aya.toArabicNumbers()}\u200F}\u200F",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    textAlign = TextAlign.Start
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Oyatning tarjimasi
                        Text(
                            text = "${ayah.aya}. ${ayah.translation}",
                            fontSize = 16.sp,
                            color = secondaryTextColor,
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
            // Background playback handles release via service lifecycle
        }
    }

    LaunchedEffect(number) {
        viewModel.getSura(number)
        viewModel.getAudioPath(number.toString())
        viewModel.getNameOfSura(number).collect {
            nameOfSura = it.englishName
        }
    }

    val surahState by viewModel.state.collectAsState()

    LaunchedEffect(surahState) {
        when (val state = surahState) {
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

    val audioPathFlow by viewModel.audioPath.collectAsState()

    LaunchedEffect(audioPathFlow) {
        audioPath = audioPathFlow
    }
}