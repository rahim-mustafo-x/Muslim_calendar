package uz.coder.muslimcalendar.data.service

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class QuranPlayerManager(private val context: Context) {

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var mediaController: MediaController? = null

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentTrackTitle = MutableStateFlow("")
    val currentTrackTitle: StateFlow<String> = _currentTrackTitle.asStateFlow()

    private val _playbackPosition = MutableStateFlow(0L)
    val playbackPosition: StateFlow<Long> = _playbackPosition.asStateFlow()

    private val _duration = MutableStateFlow(1L)
    val duration: StateFlow<Long> = _duration.asStateFlow()

    private val _isPreparing = MutableStateFlow(false)
    val isPreparing: StateFlow<Boolean> = _isPreparing.asStateFlow()

    init {
        initializeController()
        startPositionUpdates()
    }

    private fun initializeController() {
        val sessionToken = SessionToken(context, ComponentName(context, QuranAudioService::class.java))
        controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture?.addListener({
            try {
                mediaController = controllerFuture?.get()
                mediaController?.addListener(object : Player.Listener {
                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        _isPlaying.value = isPlaying
                    }

                    override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                        _currentTrackTitle.value = mediaMetadata.title?.toString() ?: ""
                    }

                    override fun onPlaybackStateChanged(playbackState: Int) {
                        _isPreparing.value = playbackState == Player.STATE_BUFFERING
                        if (playbackState == Player.STATE_READY) {
                            _duration.value = mediaController?.duration?.coerceAtLeast(1L) ?: 1L
                        }
                    }
                })
                
                // Set initial states if already running
                mediaController?.let {
                    _isPlaying.value = it.isPlaying
                    _currentTrackTitle.value = it.mediaMetadata.title?.toString() ?: ""
                    _isPreparing.value = it.playbackState == Player.STATE_BUFFERING
                    _duration.value = it.duration.coerceAtLeast(1L)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, MoreExecutors.directExecutor())
    }

    @OptIn(UnstableApi::class)
    fun playSurah(url: String, title: String) {
        val controller = mediaController ?: return
        
        // Notification backgroundini va rasmini "static" (doimiy bir xil) qilish uchun
        // ilova belgisini (icon) metadata sifatida qo'shamiz.
        val mediaMetadata = MediaMetadata.Builder()
            .setTitle(title)
            .setDisplayTitle(title)
            .setArtist("Muslim Taqvim")
            .setArtworkUri(Uri.parse("android.resource://${context.packageName}/mipmap/ic_launcher"))
            .build()

        val mediaItem = MediaItem.Builder()
            .setUri(url)
            .setMediaId(url)
            .setMediaMetadata(mediaMetadata)
            .build()

        controller.setMediaItem(mediaItem)
        controller.prepare()
        controller.play()
    }

    fun togglePlayPause() {
        val controller = mediaController ?: return
        if (controller.isPlaying) {
            controller.pause()
        } else {
            controller.play()
        }
    }

    fun seekTo(position: Long) {
        mediaController?.seekTo(position)
    }

    fun fastForward() {
        val controller = mediaController ?: return
        controller.seekTo((controller.currentPosition + 5000).coerceAtMost(controller.duration))
    }

    fun rewind() {
        val controller = mediaController ?: return
        controller.seekTo((controller.currentPosition - 5000).coerceAtLeast(0L))
    }

    private fun startPositionUpdates() {
        scope.launch {
            while (true) {
                mediaController?.let {
                    if (it.isPlaying) {
                        _playbackPosition.value = it.currentPosition
                        _duration.value = it.duration.coerceAtLeast(1L)
                    }
                }
                delay(500)
            }
        }
    }

    fun release() {
        controllerFuture?.let {
            MediaController.releaseFuture(it)
        }
    }
}
