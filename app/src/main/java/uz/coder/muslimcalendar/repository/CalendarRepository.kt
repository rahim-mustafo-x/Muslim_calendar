package uz.coder.muslimcalendar.repository

import kotlinx.coroutines.flow.Flow
import uz.coder.muslimcalendar.models.model.MuslimCalendar
import uz.coder.muslimcalendar.models.model.quran.Sura
import uz.coder.muslimcalendar.models.model.quran.Surah

interface CalendarRepository {
    suspend fun loading()
    suspend fun region(region:String)
    suspend fun remove()
    fun presentDay(): Flow<MuslimCalendar>
    fun oneMonth():Flow<List<MuslimCalendar>>
    suspend fun loadQuranArab()
    fun getSurah(): Flow<List<Sura>>
    fun getSurahByNumber(number: Int): Flow<Sura>
    fun getSura(surahNumber: Int): Flow<Surah>
}