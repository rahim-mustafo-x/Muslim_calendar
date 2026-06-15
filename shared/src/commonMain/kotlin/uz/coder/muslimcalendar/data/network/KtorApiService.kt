package uz.coder.muslimcalendar.shared.data.network

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import uz.coder.muslimcalendar.shared.data.network.modelDTO.PrayerResponse

class KtorApiService(
    private val client: HttpClient
) {
    suspend fun getOneMonthPrayerTimes(
        year: Int,
        month: Int,
        latitude: Double,
        longitude: Double,
        method: Int = 2
    ): PrayerResponse {
        return client.get("https://api.aladhan.com/v1/calendar/$year/$month") {
            parameter("latitude", latitude)
            parameter("longitude", longitude)
            parameter("method", method)
        }.body()
    }
}
