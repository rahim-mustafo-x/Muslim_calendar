package uz.coder.muslimcalendar.data.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import io.ktor.client.HttpClient
import io.ktor.client.request.prepareGet
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.contentLength
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readAvailable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import uz.coder.muslimcalendar.R
import uz.coder.muslimcalendar.data.db.AppDatabase
import uz.coder.muslimcalendar.data.db.model.AudioPathDbModel
import java.io.File
import java.io.FileOutputStream
import java.util.Locale

class DownloadWorker(
    context: Context,
    workerParams: WorkerParameters,
    private val db: AppDatabase,
    private val httpClient: HttpClient,
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val KEY_FILE_URL = "KEY_FILE_URL"
        const val KEY_SURA = "KEY_SURA"
        const val KEY_PROGRESS = "KEY_PROGRESS"
        const val TAG = "DownloadWorker"
        const val CHANNEL_ID = "download_channel"
        const val BASE_NOTIFICATION_ID = 1000
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val fileUrl = inputData.getString(KEY_FILE_URL) ?: return@withContext Result.failure()
        val suraId = inputData.getString(KEY_SURA) ?: return@withContext Result.failure()

        val surahNumber = suraId.toIntOrNull() ?: suraId.hashCode()
        val notificationId = BASE_NOTIFICATION_ID + surahNumber
        var surahName = "Sura $surahNumber"

        try {
            db.suraDao().getSuraById(surahNumber).firstOrNull()?.let {
                surahName = it.englishName
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to fetch surah name from DB", e)
        }

        ensureChannel()

        // Post initial foreground notification
        updateProgress(notificationId, surahName, "Yuklash boshlanmoqda…", 0)

        try {
            // 2. Download Audio Stream
            val filePath = downloadFile(fileUrl, notificationId, surahName)

            if (filePath.isNotEmpty()) {
                db.audioPathDao().insertAudioPath(
                    AudioPathDbModel(suraId, filePath)
                )
                showFinalNotification(notificationId, "$surahName - Yuklash yakunlandi", "100% • Muvaffaqiyatli yuklandi", 100)
                Result.success()
            } else {
                showFinalNotification(notificationId, "$surahName - Xatolik", "Yuklashda xatolik yuz berdi", 0)
                Result.failure()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Download error for sura $surahName: ${e.message}", e)
            showFinalNotification(notificationId, "$surahName - Xatolik", "Yuklashda xatolik yuz berdi", 0)
            Result.failure()
        }
    }

    private fun ensureChannel() {
        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (manager.getNotificationChannel(CHANNEL_ID) == null) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Yuklab olish",
                NotificationManager.IMPORTANCE_LOW
            )
            manager.createNotificationChannel(channel)
        }
    }

    private suspend fun updateProgress(notificationId: Int, surahName: String, status: String, progress: Int) {
        // Emit progress state back to WorkManager for UI observation
        setProgress(workDataOf(KEY_PROGRESS to progress))

        val layout = RemoteViews(applicationContext.packageName, R.layout.notification_download_progress).apply {
            setTextViewText(R.id.download_title, surahName)
            setProgressBar(R.id.download_progress_bar, 100, progress, false)
            setTextViewText(R.id.download_status, status)
        }

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(layout)
            .setCustomBigContentView(layout)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .build()

        val foregroundInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(notificationId, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        } else {
            ForegroundInfo(notificationId, notification)
        }

        try {
            setForeground(foregroundInfo)
        } catch (e: Exception) {
            Log.w(TAG, "Failed to setForeground", e)
        }

        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationId, notification)
    }

    private suspend fun downloadFile(fileUrl: String, notificationId: Int, surahName: String): String {
        val fileName = fileUrl.substringAfterLast("/")
        val file = File(applicationContext.getExternalFilesDir(null), fileName)
        val temporaryFile = File(file.parentFile, "$fileName.part")

        if (file.exists() && file.length() > 0) return file.absolutePath

        try {
            var isSuccessful = false
            httpClient.prepareGet(fileUrl).execute { response ->
                val channel: ByteReadChannel = response.bodyAsChannel()
                val totalBytes = response.contentLength() ?: -1L
                var downloadedBytes: Long = 0
                var lastProgress = -1
                var lastNotificationAt = 0L

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
                            if (progress != lastProgress && now - lastNotificationAt >= 500L) {
                                val status = "$progress% • ${formatBytes(downloadedBytes)} / ${formatBytes(totalBytes)}"
                                updateProgress(notificationId, surahName, status, progress)
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

            if (!isSuccessful || (file.exists() && !file.delete()) || !temporaryFile.renameTo(file)) {
                temporaryFile.delete()
                return ""
            }
            return file.absolutePath
        } catch (e: Exception) {
            Log.e(TAG, "Error while streaming audio file download", e)
            temporaryFile.delete()
            return ""
        }
    }

    private fun showFinalNotification(notificationId: Int, title: String, status: String, progress: Int) {
        val layout = RemoteViews(applicationContext.packageName, R.layout.notification_download_progress).apply {
            setTextViewText(R.id.download_title, title)
            setProgressBar(R.id.download_progress_bar, 100, progress, false)
            setTextViewText(R.id.download_status, status)
        }
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setCustomContentView(layout)
            .setOngoing(false)
            .build()

        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationId, notification)
    }

    private fun formatBytes(bytes: Long): String {
        if (bytes < 1024L * 1024L) return "${bytes / 1024L} KB"
        return String.format(Locale.US, "%.1f MB", bytes / (1024.0 * 1024.0))
    }
}