package uz.coder.muslimcalendar.data.network

import retrofit2.Response
import retrofit2.http.GET
import uz.coder.muslimcalendar.data.network.modelDTO.quran.ResultDTO
import uz.coder.muslimcalendar.data.network.modelDTO.quran.SuraDTO

interface ApiServiceQuranArab {
    @GET("v1/surah")
    suspend fun getQuranArab(): Response<ResultDTO<List<SuraDTO>?>>
}