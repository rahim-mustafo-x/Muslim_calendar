package uz.coder.muslimcalendar.repository

import kotlinx.coroutines.flow.Flow
import uz.coder.muslimcalendar.models.model.AudioPath
import uz.coder.muslimcalendar.models.model.MuslimCalendar
import uz.coder.muslimcalendar.models.model.SuraAyah
import uz.coder.muslimcalendar.models.model.quran.Sura
import uz.coder.muslimcalendar.models.model.quran.Surah
import uz.coder.muslimcalendar.models.model.quran.SurahList
import java.util.UUID

interface CalendarRepository {
    suspend fun loading(longitude: Double, latitude: Double)
    suspend fun region(region:String)
    suspend fun remove()
    fun presentDay(): Flow<MuslimCalendar>
    fun oneMonth():Flow<List<MuslimCalendar>>
    suspend fun loadQuranArab()
    fun getSurah(): Flow<List<Sura>>
    fun downloadSurah(suraAyahs: List<SurahList>, url: String): Flow<UUID>
    fun getSuraByNumber(number: Int): Flow<Sura>
    fun getSurahById(sura:String):Flow<List<SuraAyah>>
    fun getSura(number:Int):Flow<Surah>
    fun getAudioPath(sura:String):Flow<AudioPath>
}