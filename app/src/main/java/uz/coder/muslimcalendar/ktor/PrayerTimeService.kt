package uz.coder.muslimcalendar.ktor

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import uz.coder.muslimcalendar.models.internet.PrayerTime

interface PrayerTimeService {

    suspend fun oneMonthByRegionMonth(region:String):List<PrayerTime>?
    companion object{
        private const val BASE_URL = "https://islomapi.uz/"
        const val ONE_MONTH_BASE_URL = "${BASE_URL}api/monthly"
        val oneMonth = PrayerTimeServiceImpl(HttpClient(Android){
            install(ContentNegotiation){
                json(Json { ignoreUnknownKeys = true })
            }
        })
    }
}