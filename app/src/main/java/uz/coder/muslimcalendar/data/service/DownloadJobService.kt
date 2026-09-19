package uz.coder.muslimcalendar.data.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import io.ktor.client.HttpClient
import io.ktor.client.request.prepareGet
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.contentLength
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readAvailable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import uz.coder.muslimcalendar.R
import uz.coder.muslimcalendar.data.db.AppDatabase
import uz.coder.muslimcalendar.data.db.model.AudioPathDbModel
import uz.coder.muslimcalendar.data.map.CalendarMap
import uz.coder.muslimcalendar.domain.model.quran.SurahList
import java.io.File
import java.io.FileOutputStream
import java.util.Locale
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.cancellation.CancellationException

class DownloadJobService : Service(), KoinComponent {

    private val db: AppDatabase by inject()
    private val map: CalendarMap by inject()
    private val httpClient: HttpClient by inject(named("downloadClient"))

    private val jobScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val json = Json { ignoreUnknownKeys = true }
    private val activeDownloadsCount = AtomicInteger(0)

    companion object {
        const val KEY_FILE_URL = "KEY_FILE_URL"
        const val KEY_SURA = "KEY_SURA"
        const val TAG = "DownloadJobService"
        const val CHANNEL_ID = "download_channel"
        const val BASE_NOTIFICATION_ID = 1000
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val fileUrl = intent?.getStringExtra(KEY_FILE_URL)
        val suraJson = intent?.getStringExtra(KEY_SURA)

        if (fileUrl.isNullOrEmpty() || suraJson.isNullOrEmpty()) {
            checkStopSelf()
            return START_NOT_STICKY
        }

        val surahLists = try {
            json.decodeFromString<List<SurahList>>(suraJson)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse surah list", e)
            checkStopSelf()
            return START_NOT_STICKY
        }

        if (surahLists.isEmpty()) {
            checkStopSelf()
            return START_NOT_STICKY
        }

        val surahNumber = try {
            surahLists.first().sura.toInt()
        } catch (e: Exception) {
            surahLists.first().sura.hashCode()
        }

        val notificationId = BASE_NOTIFICATION_ID + surahNumber

        ensureChannel()

        activeDownloadsCount.incrementAndGet()

        jobScope.launch {
            var surahName = "Sura $surahNumber"
            try {
                try {
                    db.suraDao().getSuraById(surahNumber).collect {
                        surahName = it.englishName
                        throw CancellationException("Got data")
                    }
                } catch (ce: CancellationException) {
                    // expected breakout
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to fetch surah name from DB", e)
                }

                // Agar birinchi yuklanayotgan sura bo'lsa, uni xizmatning asosi (Foreground) sifatida o'rnatamiz
                if (activeDownloadsCount.get() == 1) {
                    val firstLayout = RemoteViews(packageName, R.layout.notification_download_progress).apply {
                        setTextViewText(R.id.download_title, surahName)
                        setProgressBar(R.id.download_progress_bar, 100, 0, false)
                        setTextViewText(R.id.download_status, "0% • Yuklash boshlanmoqda…")
                    }
                    val foregroundNotification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                        .setSmallIcon(android.R.drawable.stat_sys_download)
                        .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                        .setCustomContentView(firstLayout)
                        .setCustomBigContentView(firstLayout)
                        .setOngoing(true)
                        .setOnlyAlertOnce(true)
                        .build()
                    startForeground(notificationId, foregroundNotification)
                } else {
                    showStartingNotification(notificationId, surahName)
                }

                // 1. Text Download (DB Insertion)
                Log.d(TAG, "Saving surah text to database for sura $surahName...")
                db.surahAyahDao().insertAll(map.toSuraAyahDbModels(surahLists))

                // 2. Audio Download
                Log.d(TAG, "Starting audio download from: $fileUrl for sura $surahName")
                val filePath = downloadFile(fileUrl, notificationId, surahName)

                if (filePath.isNotEmpty()) {
                    db.audioPathDao().insertAudioPath(
                        AudioPathDbModel(surahLists.first().sura, filePath)
                    )
                    Log.d(TAG, "Download process completed successfully for sura $surahName.")
                } else {
                    Log.e(TAG, "Audio download failed for sura $surahName.")
                }

            } catch (e: CancellationException) {
                Log.w(TAG, "Download cancelled for sura $surahName", e)
            } catch (e: Exception) {
                Log.e(TAG, "Download error for sura $surahName: ${e.message}", e)
            } finally {
                activeDownloadsCount.decrementAndGet()
                if (activeDownloadsCount.get() == 0) {
                    stopForeground(STOP_FOREGROUND_REMOVE)
                }
                checkStopSelf()
                val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                manager.cancel(notificationId)
            }
        }

        return START_NOT_STICKY
    }

    private fun checkStopSelf() {
        if (activeDownloadsCount.get() == 0) {
            stopSelf()
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun ensureChannel() {
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (manager.getNotificationChannel(CHANNEL_ID) == null) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Yuklab olish",
                NotificationManager.IMPORTANCE_LOW
            )
            manager.createNotificationChannel(channel)
        }
    }

    private fun showStartingNotification(notificationId: Int, surahName: String) {
        val layout = RemoteViews(packageName, R.layout.notification_download_progress).apply {
            setTextViewText(R.id.download_title, surahName)
            setProgressBar(R.id.download_progress_bar, 100, 0, false)
            setTextViewText(R.id.download_status, "0% • Yuklash boshlanmoqda…")
        }
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(layout)
            .setCustomBigContentView(layout)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .build()
        
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationId, notification)
    }

    @SuppressLint("ForegroundServiceType")
    private suspend fun downloadFile(fileUrl: String, notificationId: Int, surahName: String): String = withContext(Dispatchers.IO) {
        val fileName = fileUrl.substringAfterLast("/")
        val file = File(applicationContext.getExternalFilesDir(null), fileName)
        val temporaryFile = File(file.parentFile, "$fileName.part")

        // If file already exists, we consider it "downloaded"
        if (file.exists() && file.length() > 0) return@withContext file.absolutePath

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val customLayout = RemoteViews(packageName, R.layout.notification_download_progress)
        customLayout.setTextViewText(R.id.download_title, surahName)
        customLayout.setProgressBar(R.id.download_progress_bar, 100, 0, false)
        customLayout.setTextViewText(R.id.download_status, "0% • Yuklash boshlanmoqda…")

        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(customLayout)
            .setCustomBigContentView(customLayout)
            .setOngoing(true)
            .setOnlyAlertOnce(true)

        manager.notify(notificationId, builder.build())

        try {
            var isSuccessful = false
            httpClient.prepareGet(fileUrl).execute { response ->
                val channel: ByteReadChannel = response.bodyAsChannel()
                val totalBytes = response.contentLength() ?: -1L
                var downloadedBytes: Long = 0
                var lastProgress = -1
                var lastNotificationAt = 0L

                if (totalBytes > 0L) {
                    updateProgressNotification(
                        layout = customLayout,
                        downloadedBytes = 0L,
                        totalBytes = totalBytes,
                        manager = manager,
                        builder = builder,
                        notificationId = notificationId,
                        surahName = surahName
                    )
                } else {
                    customLayout.setProgressBar(R.id.download_progress_bar, 100, 0, true)
                    customLayout.setTextViewText(R.id.download_status, "Fayl hajmi aniqlanmoqda…")
                    manager.notify(notificationId, builder.build())
                }

                FileOutputStream(temporaryFile).use { output ->
                    val buffer = ByteArray(8 * 1024)
                    while (!channel.isClosedForRead) {
                        val bytesRead = channel.readAvailable(buffer)
                        if (bytesRead == -1) break
                        if (bytesRead == 0) continue
                        output.write(buffer, 0, bytesRead)
                        downloadedBytes += bytesRead

                        if (totalBytes > 0) {
                            val progress = (downloadedBytes * 100 / totalBytes).toInt()
                            val now = System.currentTimeMillis()
                            if (progress != lastProgress && now - lastNotificationAt >= 250L) {
                                updateProgressNotification(
                                                    layout = customLayout,
                                                    downloadedBytes = downloadedBytes,
                                                    totalBytes = totalBytes,
                                                    manager = manager,
                                                    builder = builder,
                                                    notificationId = notificationId,
                                                    surahName = surahName
                                )
                                lastProgress = progress
                                lastNotificationAt = now
                            }
                        }
                    }
                    output.flush()
                }

                if (temporaryFile.length() > 0L && (totalBytes <= 0L || downloadedBytes == totalBytes)) {
                    isSuccessful = true
                }
            }

            if (!isSuccessful ||
                (file.exists() && !file.delete()) ||
                !temporaryFile.renameTo(file)
            ) {
                temporaryFile.delete()
                customLayout.setTextViewText(R.id.download_title, "$surahName - Yuklash muvaffaqiyatsiz tugadi")
                customLayout.setProgressBar(R.id.download_progress_bar, 100, 0, false)
                customLayout.setTextViewText(R.id.download_status, "Xatolik yuz berdi (Download failed)")
                builder.setOngoing(false)
                manager.notify(notificationId, builder.build())
                return@withContext ""
            }

            // Success final notification state
            customLayout.setTextViewText(R.id.download_title, "$surahName - Yuklash yakunlandi (Download completed)")
            customLayout.setProgressBar(R.id.download_progress_bar, 100, 100, false)
            customLayout.setTextViewText(R.id.download_status, "100% • Muvaffaqiyatli yuklandi")
            builder.setOngoing(false)
            manager.notify(notificationId, builder.build())

            delay(1000)
            stopForeground(STOP_FOREGROUND_DETACH)
            return@withContext file.absolutePath

        } catch (e: Exception) {
            Log.e(TAG, "Download error during file write: ${e.message}")
            temporaryFile.delete()
            customLayout.setTextViewText(R.id.download_title, "$surahName - Yuklash muvaffaqiyatsiz tugadi")
            customLayout.setProgressBar(R.id.download_progress_bar, 100, 0, false)
            customLayout.setTextViewText(R.id.download_status, "Xatolik yuz berdi (Download failed)")
            builder.setOngoing(false)
            manager.notify(notificationId, builder.build())
            stopForeground(STOP_FOREGROUND_DETACH)
            return@withContext ""
        }
    }

    private fun updateProgressNotification(
        layout: RemoteViews,
        downloadedBytes: Long,
        totalBytes: Long,
        manager: NotificationManager,
        builder: NotificationCompat.Builder,
        notificationId: Int,
        surahName: String
    ) {
        val progress = (downloadedBytes * 100 / totalBytes).toInt().coerceIn(0, 100)
        val remainingBytes = (totalBytes - downloadedBytes).coerceAtLeast(0L)
        layout.setTextViewText(R.id.download_title, surahName)
        layout.setProgressBar(R.id.download_progress_bar, 100, progress, false)
        layout.setTextViewText(
            R.id.download_status,
            "$progress% • ${formatBytes(downloadedBytes)} / ${formatBytes(totalBytes)} • ${formatBytes(remainingBytes)} qoldi"
        )
        manager.notify(notificationId, builder.build())
    }

    private fun formatBytes(bytes: Long): String {
        if (bytes < 1024L * 1024L) return "${bytes / 1024L} KB"
        return String.format(Locale.US, "%.1f MB", bytes / (1024.0 * 1024.0))
    }

    override fun onDestroy() {
        super.onDestroy()
        jobScope.cancel()
    }
}
