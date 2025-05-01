package uz.coder.muslimcalendar.ktor

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import uz.coder.muslimcalendar.ktor.PrayerTimeService.Companion.ONE_MONTH_BASE_URL
import uz.coder.muslimcalendar.models.internet.PrayerData
import uz.coder.muslimcalendar.models.internet.PrayerResponse
import java.util.Calendar

data class PrayerTimeServiceImpl(
    private val httpClient: HttpClient
):PrayerTimeService{
    override suspend fun oneMonth(latitude: Double, longitude: Double): PrayerResponse<List<PrayerData>>? {
        val instance = Calendar.getInstance()
        val month = instance.get(Calendar.MONTH)+1
        val year = instance.get(Calendar.YEAR)
        Log.d(
            TAG,
            "oneMonth: ${
                httpClient.get("$ONE_MONTH_BASE_URL/$year/$month?latitude=$latitude&longitude=$longitude&method=2")
                    .bodyAsText()
            }"
        )
        return withContext(Dispatchers.IO){ httpClient.get("$ONE_MONTH_BASE_URL/$year/$month?latitude=$latitude&longitude=$longitude&method=2").body() }
    }
}

private const val TAG = "PrayerTimeServiceImpl"