package uz.coder.muslimcalendar.data.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.job.JobParameters
import android.app.job.JobService
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
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
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import uz.coder.muslimcalendar.R
import uz.coder.muslimcalendar.data.db.AppDatabase
import uz.coder.muslimcalendar.data.db.model.AudioPathDbModel
import uz.coder.muslimcalendar.data.map.CalendarMap
import uz.coder.muslimcalendar.domain.model.quran.SurahList
import java.io.File
import java.io.FileOutputStream
import kotlin.coroutines.cancellation.CancellationException

@Suppress("DEPRECATION")
@SuppressLint("SpecifyJobSchedulerIdRange")
class DownloadJobService : JobService(), KoinComponent {

    private val db: AppDatabase by inject()
    private val map: CalendarMap by inject()
    private val httpClient: HttpClient by inject()

    private val jobScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val json = Json { ignoreUnknownKeys = true }

    companion object {
        const val KEY_FILE_URL = "KEY_FILE_URL"
        const val KEY_SURA = "KEY_SURA"
        const val TAG = "DownloadJobService"
        const val CHANNEL_ID = "download_channel"
        const val NOTIFICATION_ID = 1001
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

        ensureChannel()

        jobScope.launch {
            try {
                // 1. Text Download (DB Insertion)
                Log.d(TAG, "Saving surah text to database...")
                db.surahAyahDao().insertAll(map.toSuraAyahDbModels(surahLists))

                // 2. Audio Download
                Log.d(TAG, "Starting audio download from: $fileUrl")
                val filePath = downloadFile(fileUrl)

                if (filePath.isNotEmpty()) {
                    db.audioPathDao().insertAudioPath(
                        AudioPathDbModel(surahLists.first().sura, filePath)
                    )
                    Log.d(TAG, "Download process completed successfully.")
                    jobFinished(params, false)
                } else {
                    Log.e(TAG, "Audio download failed.")
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
        return true
    }

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

    @SuppressLint("ForegroundServiceType")
    private suspend fun downloadFile(fileUrl: String): String = withContext(Dispatchers.IO) {
        val fileName = fileUrl.substringAfterLast("/")
        val file = File(applicationContext.getExternalFilesDir(null), fileName)

        // If file already exists, we consider it "downloaded"
        if (file.exists() && file.length() > 0) return@withContext file.absolutePath

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val customLayout = RemoteViews(packageName, R.layout.notification_download_progress)
        
        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(customLayout)
            .setOngoing(true)
            .setOnlyAlertOnce(true)

        startForeground(NOTIFICATION_ID, builder.build())

        try {
            val response: HttpResponse = httpClient.get(fileUrl)
            val channel: ByteReadChannel = response.bodyAsChannel()
            val totalBytes = response.contentLength() ?: -1L
            var downloadedBytes: Long = 0
            var lastProgress = -1

            FileOutputStream(file).use { output ->
                val buffer = ByteArray(8 * 1024)
                while (!channel.isClosedForRead) {
                    val bytesRead = channel.readAvailable(buffer)
                    if (bytesRead == -1) break
                    output.write(buffer, 0, bytesRead)
                    downloadedBytes += bytesRead

                    if (totalBytes > 0) {
                        val progress = (downloadedBytes * 100 / totalBytes).toInt()
                        if (progress != lastProgress) {
                            customLayout.setProgressBar(R.id.download_progress_bar, 100, progress, false)
                            customLayout.setTextViewText(R.id.download_status, "$progress%")
                            manager.notify(NOTIFICATION_ID, builder.build())
                            lastProgress = progress
                        }
                    } else {
                        customLayout.setProgressBar(R.id.download_progress_bar, 100, 0, true)
                        customLayout.setTextViewText(R.id.download_status, "Yuklanmoqda...")
                        manager.notify(NOTIFICATION_ID, builder.build())
                    }
                }
                output.flush()
            }

            // Success final notification state
            customLayout.setTextViewText(R.id.download_title, "Yuklash yakunlandi")
            customLayout.setProgressBar(R.id.download_progress_bar, 100, 100, false)
            customLayout.setTextViewText(R.id.download_status, "100%")
            builder.setOngoing(false)
            manager.notify(NOTIFICATION_ID, builder.build())

            delay(1000)
            stopForeground(true)
            return@withContext file.absolutePath

        } catch (e: Exception) {
            Log.e(TAG, "Download error during file write: ${e.message}")
            if (file.exists()) file.delete()
            stopForeground(true)
            return@withContext ""
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        jobScope.cancel()
    }
}
