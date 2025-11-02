package uz.coder.muslimcalendar.data.service

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import okhttp3.OkHttpClient
import okhttp3.Request
import uz.coder.muslimcalendar.data.db.AppDatabase
import uz.coder.muslimcalendar.data.db.model.AudioPathDbModel
import java.io.File
import java.io.FileOutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class DownloadWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val db: AppDatabase,
    private val okHttpClient: OkHttpClient
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val fileUrl = inputData.getString(KEY_FILE_URL) ?: return@withContext Result.failure()
        val sura = inputData.getString(KEY_SURA) ?: return@withContext Result.failure()

        try {
            val filePath = downloadFile(fileUrl) { progress ->
                setProgress(workDataOf(KEY_PROGRESS to progress))
            }

            if (filePath.isNotEmpty()) {
                db.audioPathDao().insertAudioPath(AudioPathDbModel(sura, filePath))
                Log.d(TAG, "File downloaded and saved: $filePath")
                Result.success(workDataOf("filePath" to filePath))
            } else {
                Log.e(TAG, "File download failed")
                Result.retry()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Download error: ${e.message}", e)
            Result.retry()
        }
    }

    private suspend fun downloadFile(
        fileUrl: String,
        progressCallback: suspend (Int) -> Unit
    ): String = withContext(Dispatchers.IO) {
        val fileName = fileUrl.substringAfterLast("/")
        val file = File(context.getExternalFilesDir(null), fileName)

        if (file.exists()) {
            progressCallback(100)
            return@withContext file.absolutePath
        }

        try {
            val request = Request.Builder().url(fileUrl).build()
            val response = okHttpClient.newCall(request).execute()

            if (!response.isSuccessful) {
                Log.e(TAG, "HTTP error code: ${response.code}")
                return@withContext ""
            }

            val body = response.body
            val fileLength = body.contentLength().takeIf { it > 0 }
            val buffer = ByteArray(8 * 1024)
            var totalRead = 0L
            var count: Int

            body.byteStream().use { input ->
                FileOutputStream(file).use { fos ->
                    while (input.read(buffer).also { count = it } != -1) {
                        fos.write(buffer, 0, count)
                        totalRead += count

                        val percent = fileLength?.let { (totalRead * 100 / it).toInt() } ?: 0
                        progressCallback(percent)
                    }
                    fos.flush()
                }
            }

            progressCallback(100)
            return@withContext file.absolutePath
        } catch (e: Exception) {
            file.delete()
            Log.e(TAG, "Exception while downloading: ${e.message}", e)
            return@withContext ""
        }
    }

    companion object {
        const val KEY_FILE_URL = "KEY_FILE_URL"
        const val KEY_SURA = "KEY_SURA"
        const val KEY_PROGRESS = "progress"
        private const val TAG = "DownloadWorker"
    }
}