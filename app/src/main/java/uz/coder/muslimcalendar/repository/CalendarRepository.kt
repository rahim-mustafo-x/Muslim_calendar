package uz.coder.muslimcalendar.repository

import kotlinx.coroutines.flow.Flow
import uz.coder.muslimcalendar.models.internet.quran.QuranDTO
import uz.coder.muslimcalendar.models.internet.quran.ResultDTO
import uz.coder.muslimcalendar.models.internet.quran.SurahDTO
import uz.coder.muslimcalendar.models.model.MuslimCalendar
import uz.coder.muslimcalendar.models.model.quran.Quran
import uz.coder.muslimcalendar.models.model.quran.Surah

interface CalendarRepository {
    suspend fun loading()
    suspend fun region(region:String)
    suspend fun remove()
    fun presentDay(): Flow<MuslimCalendar>
    fun oneMonth():Flow<List<MuslimCalendar>>
    fun getQuranArab(): Flow<Quran>
    fun getSura(surahNumber: Int): Flow<Surah>
}