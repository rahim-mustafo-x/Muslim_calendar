package uz.coder.muslimcalendar.repository

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import kotlinx.coroutines.flow.channelFlow
import uz.coder.muslimcalendar.R
import uz.coder.muslimcalendar.db.AppDatabase
import uz.coder.muslimcalendar.ktor.PrayerTimeService
import uz.coder.muslimcalendar.todo.REGION
import java.util.Calendar.DAY_OF_MONTH
import java.util.Calendar.MONTH
import java.util.Calendar.getInstance
import androidx.core.content.edit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import uz.coder.muslimcalendar.ktor.ApiService.Companion.apiService
import uz.coder.muslimcalendar.map.CalendarMap
import uz.coder.muslimcalendar.models.model.quran.Sura
import uz.coder.muslimcalendar.models.model.quran.Surah
import java.io.IOException
import java.time.LocalDate

data class CalendarRepositoryImpl(private val application: Application):CalendarRepository {
    private val preferences:SharedPreferences by lazy { application.getSharedPreferences(application.getString(R.string.app_name), Context.MODE_PRIVATE) }
    private val db:AppDatabase by lazy { AppDatabase.instance(application) }
    private val map = CalendarMap()

    override suspend fun loading(longitude: Double, latitude: Double) {
        Log.d(TAG, "loading: $longitude/$latitude")
        if (longitude != 0.0 && latitude != 0.0) {
            try {
                val result = withContext(Dispatchers.IO) {
                    PrayerTimeService.oneMonth.oneMonth(latitude, longitude)
                }
                result.data?.let {
                    db.calendarDao().insertMuslimCalendar(map.toMuslimCalendarDbModel(it))
                    Log.d(TAG, "loading: $it")
                } ?: Log.d(TAG, "loading: bo'sh")
            } catch (e: IOException) {
                Log.e(TAG, "loading: Tarmoq xatoligi", e)
            } catch (e: Exception) {
                Log.e(TAG, "loading: Noma'lum xatolik", e)
            }
        } else {
            Log.d(TAG, "loading: koordinatalar bo'sh")
        }
    }


    override suspend fun region(region: String) {
        preferences.edit { putString(REGION, region) }
    }

    override suspend fun remove() {
        val calendar = getInstance()
        val month = calendar.get(MONTH)+1
        val day = calendar.get(DAY_OF_MONTH)
        db.calendarDao().refreshDay().collect{
            if (it.day == day && it.month == month){
                db.calendarDao().deleteCalendar()
            }
        }
    }

    override fun presentDay() = flow {
        val localeDate = LocalDate.now()
        val presentDay =
            db.calendarDao().presentDay(localeDate.dayOfMonth, localeDate.monthValue)
           presentDay.collect{
               Log.d(TAG, "presentDay: $it")
                   try {
                       emit(
                           map.toMuslimCalendar(it)
                       )
                   }catch (_:Exception){}
           }
    }

    override fun oneMonth() = channelFlow {
        db.calendarDao().oneMonth().collect{
            send(map.toMuslimCalendarList(it))
        }
    }

    override suspend fun loadQuranArab() {
        val result = withContext(Dispatchers.IO) { apiService.getQuranArab() }
        db.suraDao().insertAll(
            map.toSuraDbModel(result.data?.surahs)
        )
    }

    override fun getSurah() = flow<List<Sura>> {
        db.suraDao().getAllSura().collect{
            emit(
                map.toSuraList(it)
            )
        }
    }

    override fun getSurahByNumber(number: Int) = flow<Sura> {
        db.suraDao().getSuraById(number).collect{
            emit(
                map.toSura(it)
            )
        }
    }

    override fun getSura(surahNumber: Int) = flow<Surah> {
        val result = withContext(Dispatchers.IO) { apiService.getSura(surahNumber) }
        emit(
            Surah(
                map.toSurahList(result.result)
            )
        )
    }
}

private const val TAG = "CalendarRepositoryImpl"