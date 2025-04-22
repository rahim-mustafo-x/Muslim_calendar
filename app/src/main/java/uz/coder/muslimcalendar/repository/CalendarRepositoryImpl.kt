package uz.coder.muslimcalendar.repository

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import uz.coder.muslimcalendar.R
import uz.coder.muslimcalendar.db.MuslimCalendarDatabase
import uz.coder.muslimcalendar.ktor.PrayerTimeService
import uz.coder.muslimcalendar.models.db.MuslimCalendarDbModel
import uz.coder.muslimcalendar.models.model.MuslimCalendar
import uz.coder.muslimcalendar.todo.DEFAULT_REGION
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
import uz.coder.muslimcalendar.models.model.quran.Quran
import uz.coder.muslimcalendar.models.model.quran.Surah

data class CalendarRepositoryImpl(private val application: Application):CalendarRepository {
    private val preferences:SharedPreferences by lazy { application.getSharedPreferences(application.getString(R.string.app_name), Context.MODE_PRIVATE) }
    private val db:MuslimCalendarDatabase by lazy { MuslimCalendarDatabase.instance(application) }
    private val map = CalendarMap()

    override suspend fun loading() {
        try {
            val result = PrayerTimeService.oneMonth.oneMonthByRegionMonth(
                preferences.getString(
                    REGION,
                    DEFAULT_REGION
                ) ?: DEFAULT_REGION
            )
            if (result != null){
                    db.calendarDao().insertMuslimCalendar(
                        map.toMuslimCalendarDbModel(result)
                    )
            }
        }catch (_:Exception){}
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

    override fun presentDay() = channelFlow {
        val calendar = getInstance()
        val presentDay =
            db.calendarDao().presentDay(calendar.get(DAY_OF_MONTH), calendar.get(MONTH) + 1)
           presentDay.collectLatest{
               Log.d("TAG", "presentDay: $it")
                   try {
                       send(
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

    override fun getQuranArab() = flow<Quran> {
        val result = withContext(Dispatchers.IO) { apiService.getQuranArab() }
        emit(
            Quran(
                map.toSuraList(result.data)
            )
        )
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