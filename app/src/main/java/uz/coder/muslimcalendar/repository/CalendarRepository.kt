package uz.coder.muslimcalendar.repository

import kotlinx.coroutines.flow.Flow
import uz.coder.muslimcalendar.models.model.MuslimCalendar
import uz.coder.muslimcalendar.models.model.SuraAyah
import uz.coder.muslimcalendar.models.model.quran.Sura
import uz.coder.muslimcalendar.models.model.quran.Surah
import uz.coder.muslimcalendar.models.model.quran.SurahList

interface CalendarRepository {
    suspend fun loading(longitude: Double, latitude: Double)
    suspend fun region(region:String)
    suspend fun remove()
    fun presentDay(): Flow<MuslimCalendar>
    fun oneMonth():Flow<List<MuslimCalendar>>
    suspend fun loadQuranArab()
    fun getSurah(): Flow<List<Sura>>
    suspend fun downloadSurah(suraAyahs: List<SurahList>, url: String)
    fun getSurahById(sura:String):Flow<List<SuraAyah>>
    fun getSura(number:Int, audioPath:String):Flow<Surah>
}