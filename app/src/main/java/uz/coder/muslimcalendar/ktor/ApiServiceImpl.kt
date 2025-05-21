package uz.coder.muslimcalendar.ktor

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import uz.coder.muslimcalendar.ktor.internet.quran.QuranDTO
import uz.coder.muslimcalendar.ktor.internet.quran.ResultDTO
import uz.coder.muslimcalendar.ktor.internet.quran.SurahDTO

data class ApiServiceImpl(private val httpClient: HttpClient): ApiService {

    override suspend fun getQuranArab(): ResultDTO<QuranDTO> {
        val url = ApiService.BASE_URL_SURAH_LIST + ApiService.QURAN_ARAB
        val response = withContext(Dispatchers.IO) { httpClient.get(url).body<ResultDTO<QuranDTO>>() }
        return response
    }

    override suspend fun getSura(surahNumber: Int):SurahDTO {
        val url = ApiService.BASE_URL_OF_PERFECT_TRANSLATION + ApiService.UZB_TRANSLATION+"/$surahNumber"
        val response = withContext(Dispatchers.IO) { httpClient.get(url).body<SurahDTO>() }
        return response
    }
}
