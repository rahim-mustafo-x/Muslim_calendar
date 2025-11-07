package uz.coder.muslimcalendar.data.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.job.JobParameters
import android.app.job.JobService
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import uz.coder.muslimcalendar.R
import uz.coder.muslimcalendar.data.db.AppDatabase
import uz.coder.muslimcalendar.data.db.model.AudioPathDbModel
import uz.coder.muslimcalendar.data.map.CalendarMap
import uz.coder.muslimcalendar.domain.model.quran.SurahList
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@SuppressLint("SpecifyJobSchedulerIdRange")
@AndroidEntryPoint
class DownloadJobService : JobService() {

    @Inject lateinit var db: AppDatabase
    @Inject lateinit var gson: Gson
    @Inject lateinit var map: CalendarMap

    private val jobScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val okHttpClient = OkHttpClient()

    companion object {
        const val KEY_FILE_URL = "KEY_FILE_URL"
        const val KEY_SURA = "KEY_SURA"
        const val TAG = "DownloadJobService"
        const val CHANNEL_ID = "download_channel"
    }

    override fun onStartJob(params: JobParameters?): Boolean {
        val fileUrl = params?.extras?.getString(KEY_FILE_URL)
        val suraJson = params?.extras?.getString(KEY_SURA)
        val surahLists =
            gson.fromJson<List<SurahList>>(suraJson, object : TypeToken<List<SurahList>>() {}.type)

        if (fileUrl.isNullOrEmpty() || surahLists.isNullOrEmpty()) {
            jobFinished(params, false)
            return false
        }

        createNotificationChannel()

        jobScope.launch {
            try {
                if (surahLists.isNotEmpty()){
                    db.surahAyahDao().insertAll(map.toSuraAyahDbModels(surahLists))
                }
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
            } catch (e: Exception) {
                Log.e(TAG, "Download error: ${e.message}", e)
                jobFinished(params, true)
            }
        }

        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        jobScope.coroutineContext.cancelChildren()
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

        try {
            val request = Request.Builder().url(fileUrl).build()
            val response = okHttpClient.newCall(request).execute()
            if (!response.isSuccessful) return@withContext ""

            val body = response.body
            val totalBytes = body.contentLength()
            var downloadedBytes: Long = 0

            body.byteStream().use { input ->
                FileOutputStream(file).use { output ->
                    val buffer = ByteArray(8 * 1024)
                    var read: Int
                    while (input.read(buffer).also { read = it } != -1) {
                        output.write(buffer, 0, read)
                        downloadedBytes += read

                        val progress = if (totalBytes > 0) (downloadedBytes * 100 / totalBytes).toInt() else 0
                        builder.setProgress(100, progress, false)
                        manager.notify(1, builder.build())
                    }
                    output.flush()
                }
            }

            builder.setProgress(0, 0, false)
                .setOngoing(false)
                .setContentText(getString(R.string.downloadCompleted))
            manager.notify(1, builder.build())

            kotlinx.coroutines.delay(1000)
            manager.cancel(1)

            return@withContext file.absolutePath
        } catch (e: Exception) {
            file.delete()
            Log.e(TAG, "Exception while downloading: ${e.message}", e)
            manager.cancel(1)
            return@withContext ""
        }
    }
}