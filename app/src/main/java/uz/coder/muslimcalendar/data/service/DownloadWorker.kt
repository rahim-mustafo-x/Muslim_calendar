package uz.coder.muslimcalendar.data.service

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import okhttp3.OkHttpClient
import okhttp3.Request
import uz.coder.muslimcalendar.db.AppDatabase
import uz.coder.muslimcalendar.db.model.AudioPathDbModel
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit

class DownloadWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val db = AppDatabase.instance(context)
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    override suspend fun doWork(): Result {
        val fileUrl = inputData.getString(KEY_FILE_URL) ?: return Result.failure()
        val sura = inputData.getString(KEY_SURA) ?: return Result.failure()

        return try {
            val filePath = downloadFile(fileUrl) { progress ->
                setProgress(workDataOf(KEY_PROGRESS to progress))
            }

            if (filePath.isNotEmpty()) {
                db.audioPathDao().insertAudioPath(AudioPathDbModel(sura, filePath))
                Log.d(TAG, "File downloaded and saved: $filePath")
                Result.success(workDataOf("filePath" to filePath))
            } else {
                Log.e(TAG, "File path is empty or failed")
                Result.retry()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Download error: ${e.message}", e)
            Result.retry()
        }
    }

    /**
     * @param progressCallback progress foizini qaytaradi (0‒100)
     * @return saqlangan faylning to‘liq yo‘li yoki "" (xato)
     */
    private suspend fun downloadFile(
        fileUrl: String,
        progressCallback: suspend (Int) -> Unit
    ): String {
        val fileName = fileUrl.substringAfterLast("/")
        val file = File(context.getExternalFilesDir(null), fileName)

        if (file.exists()) {
            progressCallback(100) // allaqachon bor ⇒ 100 %
            return file.absolutePath
        }

        try {
            val request = Request.Builder().url(fileUrl).build()
            val response = okHttpClient.newCall(request).execute()

            if (!response.isSuccessful) {
                Log.e(TAG, "HTTP error code: ${response.code}")
                return ""
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

                        val percent = if (fileLength != null) {
                            (totalRead * 100 / fileLength).toInt()
                        } else {
                            // Fayl uzunligi noma'lum bo'lsa, progressni minimal qilib yuboramiz
                            0
                        }

                        progressCallback(percent)
                    }
                    fos.flush()
                }
            }

            progressCallback(100) // tugagani haqida signal
            return file.absolutePath
        } catch (e: Exception) {
            file.delete()
            Log.e(TAG, "Exception while downloading: ${e.message}", e)
            return ""
        } finally {
            // OkHttp automatically closes the response body when using 'use'
        }
    }
    companion object {
        const val KEY_FILE_URL = "KEY_FILE_URL"
        const val KEY_SURA = "KEY_SURA"
        const val KEY_PROGRESS = "progress" // UI kuzatadigan kalit
        private const val TAG = "DownloadWorker"
    }
}