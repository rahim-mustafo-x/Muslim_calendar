package uz.coder.muslimcalendar.data.network

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import uz.coder.muslimcalendar.data.network.modelDTO.PrayerResponse
import uz.coder.muslimcalendar.data.network.modelDTO.quran.ResultDTO
import uz.coder.muslimcalendar.data.network.modelDTO.quran.SuraDTO
import uz.coder.muslimcalendar.data.network.modelDTO.quran.SurahDTO

class KtorApiService(
    private val prayerTimeClient: HttpClient,
    private val quranArabClient: HttpClient,
    private val quranUzbekClient: HttpClient
) {
    // Prayer Time API
    suspend fun getOneMonthPrayerTimes(
        year: Int,
        month: Int,
        latitude: Double,
        longitude: Double,
        method: Int = 2
    ): PrayerResponse {
        return prayerTimeClient.get("/v1/calendar/$year/$month") {
            parameter("latitude", latitude)
            parameter("longitude", longitude)
            parameter("method", method)
        }.body()
    }

    // Quran Arab API
    suspend fun getQuranArab(): ResultDTO<List<SuraDTO>?> {
        return quranArabClient.get("/v1/surah").body()
    }

    // Quran Uzbek API
    suspend fun getSura(surahNumber: Int): SurahDTO {
        return quranUzbekClient.get("/api/v1/translation/sura/uzbek_mansour/$surahNumber").body()
    }
}
