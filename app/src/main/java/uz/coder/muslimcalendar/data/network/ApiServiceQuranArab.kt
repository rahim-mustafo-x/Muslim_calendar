package uz.coder.muslimcalendar.data.network

import retrofit2.http.GET
import uz.coder.muslimcalendar.data.network.modelDTO.quran.QuranDTO
import uz.coder.muslimcalendar.data.network.modelDTO.quran.ResultDTO

interface ApiServiceQuranArab {
    //BASE_URL_SURAH_LIST
    @GET("v1/quran/quran-uthmani")
    suspend fun getQuranArab(): ResultDTO<QuranDTO>
}