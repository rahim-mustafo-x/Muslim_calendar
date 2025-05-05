package uz.coder.muslimcalendar.service

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import uz.coder.muslimcalendar.db.AppDatabase
import uz.coder.muslimcalendar.ktor.ApiService
import uz.coder.muslimcalendar.map.CalendarMap
import uz.coder.muslimcalendar.todo.isConnected

class QuranWorkManager(private val context: Context, params: WorkerParameters): CoroutineWorker(context, params) {
    private val db: AppDatabase by lazy { AppDatabase.Companion.instance(context) }
    private val map = CalendarMap()
    override suspend fun doWork(): Result {
        return try {
           if (context.isConnected()){
               db.suraDao().deleteAll()
               val result =
                   withContext(Dispatchers.IO) { ApiService.Companion.apiService.getQuranArab() }
               if (result.data?.surahs != null) {
                   db.suraDao().insertAll(map.toSuraDbModel(result.data.surahs))
               }
           }else{
               Result.retry()
           }
            Result.success()
        }catch (_: Exception){
            Result.failure()
        }
    }
}