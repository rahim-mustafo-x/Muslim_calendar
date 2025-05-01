package uz.coder.muslimcalendar.ktor

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import uz.coder.muslimcalendar.models.internet.PrayerData
import uz.coder.muslimcalendar.models.internet.PrayerResponse

interface PrayerTimeService {

    suspend fun oneMonth(latitude: Double, longitude: Double):PrayerResponse<List<PrayerData>?>
    companion object{
        private const val BASE_URL = "https://api.aladhan.com/"
        const val ONE_MONTH_BASE_URL = "${BASE_URL}/v1/calendar/"
        val oneMonth = PrayerTimeServiceImpl(HttpClient(Android){
            install(ContentNegotiation){
                json(Json { ignoreUnknownKeys = true })
            }
        })
    }
}