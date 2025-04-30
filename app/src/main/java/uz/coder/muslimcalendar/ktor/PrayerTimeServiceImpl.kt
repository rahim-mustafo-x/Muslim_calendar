package uz.coder.muslimcalendar.ktor

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import uz.coder.muslimcalendar.ktor.PrayerTimeService.Companion.ONE_MONTH_BASE_URL
import uz.coder.muslimcalendar.models.internet.PrayerTime
import java.util.Calendar

data class PrayerTimeServiceImpl(
    private val httpClient: HttpClient
):PrayerTimeService{
    override suspend fun oneMonthByRegionMonth(region: String): List<PrayerTime>? {
        val instance = Calendar.getInstance()
        val month = instance.get(Calendar.MONTH)+1
        return withContext(Dispatchers.IO){ httpClient.get("$ONE_MONTH_BASE_URL?region=$region&month=$month").body() }
    }
}