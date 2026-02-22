package uz.coder.muslimcalendar.data.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.job.JobParameters
import android.app.job.JobService
import android.util.Log
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.request.get
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
import uz.coder.muslimcalendar.R
import uz.coder.muslimcalendar.data.db.AppDatabase
import uz.coder.muslimcalendar.data.db.model.AudioPathDbModel
import uz.coder.muslimcalendar.data.map.CalendarMap
import uz.coder.muslimcalendar.domain.model.quran.SurahList
import java.io.File
import java.io.FileOutputStream
import javax.inject.Named
import kotlin.coroutines.cancellation.CancellationException

@Suppress("DEPRECATION")
@SuppressLint("SpecifyJobSchedulerIdRange")
class DownloadJobService : JobService() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface DownloadJobServiceEntryPoint {
        fun getDatabase(): AppDatabase
        fun getCalendarMap(): CalendarMap
        @Named("download")
        fun getHttpClient(): HttpClient
    }

    private lateinit var db: AppDatabase
    private lateinit var map: CalendarMap
    private lateinit var httpClient: HttpClient

    private val jobScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val json = Json { ignoreUnknownKeys = true }

    companion object {
        const val KEY_FILE_URL = "KEY_FILE_URL"
        const val KEY_SURA = "KEY_SURA"
        const val TAG = "DownloadJobService"
        const val CHANNEL_ID = "download_channel"
        const val NOTIFICATION_ID = 1
    }

    override fun onCreate() {
        super.onCreate()
        val entryPoint = EntryPointAccessors.fromApplication(
            applicationContext,
            DownloadJobServiceEntryPoint::class.java
        )
        db = entryPoint.getDatabase()
        map = entryPoint.getCalendarMap()
        httpClient = entryPoint.getHttpClient()
    }

    override fun onStartJob(params: JobParameters?): Boolean {
        if (params == null) return false

        val fileUrl = params.extras.getString(KEY_FILE_URL)
        val suraJson = params.extras.getString(KEY_SURA)

        if (fileUrl.isNullOrEmpty() || suraJson.isNullOrEmpty()) {
            jobFinished(params, false)
            return false
        }

        val surahLists = try {
            json.decodeFromString<List<SurahList>>(suraJson)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse surah list", e)
            jobFinished(params, false)
            return false
        }

        if (surahLists.isEmpty()) {
            jobFinished(params, false)
            return false
        }

        createNotificationChannel()

        jobScope.launch {
            try {
                // Insert surah data into DB
                if (surahLists.isNotEmpty()) {
                    db.surahAyahDao().insertAll(map.toSuraAyahDbModels(surahLists))
                }

                // Start download
                val filePath = downloadFile(fileUrl)

                if (filePath.isNotEmpty()) {
                    db.audioPathDao().insertAudioPath(
                        AudioPathDbModel(surahLists.first().sura, filePath)
                    )
                    Log.d(TAG, "File downloaded and saved: $filePath")
                    jobFinished(params, false)
                } else {
                    Log.e(TAG, "File download failed")
                    jobFinished(params, true)
                }

            } catch (e: CancellationException) {
                Log.w(TAG, "Download cancelled", e)
                jobFinished(params, false)
            } catch (e: Exception) {
                Log.e(TAG, "Download error: ${e.message}", e)
                jobFinished(params, true)
            }
        }

        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        // Allow coroutine to cancel naturally
        return true
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Sura Download",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = getString(R.string.proggressOfDownload)
        }
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    @SuppressLint("ForegroundServiceType")
    private suspend fun downloadFile(fileUrl: String): String = withContext(Dispatchers.IO) {
        val fileName = fileUrl.substringAfterLast("/")
        val file = File(applicationContext.getExternalFilesDir(null), fileName)

        if (file.exists()) return@withContext file.absolutePath

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val builder = Notification.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle(getString(R.string.suraIsDownloading))
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setProgress(100, 0, false)

        // Promote service to foreground
        startForeground(NOTIFICATION_ID, builder.build())

        try {
            val response: HttpResponse = httpClient.get(fileUrl)
            val channel: ByteReadChannel = response.bodyAsChannel()
            val totalBytes = response.contentLength() ?: -1L
            var downloadedBytes: Long = 0
            var lastProgress = -1

            FileOutputStream(file).use { output ->
                val buffer = ByteArray(8 * 1024) // 8 KB
                while (!channel.isClosedForRead) {
                    val bytesRead = channel.readAvailable(buffer)
                    if (bytesRead == -1) break
                    output.write(buffer, 0, bytesRead)
                    downloadedBytes += bytesRead

                    if (totalBytes > 0) {
                        val progress = (downloadedBytes * 100 / totalBytes).toInt()
                        if (progress != lastProgress) {
                            builder.setProgress(100, progress, false)
                            manager.notify(NOTIFICATION_ID, builder.build())
                            lastProgress = progress
                        }
                    } else {
                        builder.setProgress(0, 0, true)
                        manager.notify(NOTIFICATION_ID, builder.build())
                    }
                }
                output.flush()
            }

            // Final notification
            builder.setProgress(0, 0, false)
                .setOngoing(false)
                .setContentText(getString(R.string.downloadCompleted))
            manager.notify(NOTIFICATION_ID, builder.build())

            delay(1000)
            stopForeground(true) // stop foreground
            return@withContext file.absolutePath

        } catch (e: Exception) {
            Log.e(TAG, "Download failed", e)
            if (file.exists()) file.delete()
            stopForeground(true)
            return@withContext ""
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        jobScope.cancel() // cancel all coroutines
    }
}