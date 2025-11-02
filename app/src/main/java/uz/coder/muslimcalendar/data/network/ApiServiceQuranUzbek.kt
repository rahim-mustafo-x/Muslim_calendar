package uz.coder.muslimcalendar.data.network

import retrofit2.http.GET
import retrofit2.http.Path
import uz.coder.muslimcalendar.data.network.modelDTO.quran.SurahDTO

interface ApiServiceQuranUzbek {
    @GET("api/v1/translation/sura/uzbek_mansour/{surahNumber}")
    suspend fun getSura(@Path("surahNumber") surahNumber: Int): SurahDTO
}