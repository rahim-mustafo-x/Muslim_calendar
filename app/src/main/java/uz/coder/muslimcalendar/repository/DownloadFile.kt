package uz.coder.muslimcalendar.repository

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

class DownloadFile {
    suspend fun downloadFile(context: Context, fileUrl: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                val fileName = fileUrl.substringAfterLast("/")
                val url = URL(fileUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.connect()

                if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                    Log.e("Download", "Server returned HTTP ${connection.responseCode}")
                    return@withContext null
                }

                val inputStream = connection.inputStream
                val file = File(context.getExternalFilesDir(null), fileName)
                val outputStream = FileOutputStream(file)

                val buffer = ByteArray(4096)
                var bytesRead: Int
                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                }

                outputStream.close()
                inputStream.close()
                Log.d("Download", "File saved to ${file.absolutePath}")
                return@withContext file.absolutePath
            } catch (e: Exception) {
                Log.e("Download", "Error: ${e.message}")
                return@withContext null
            }
        }
    }
}
