package uz.coder.muslimcalendar.data.network

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import uz.coder.muslimcalendar.data.network.modelDTO.PrayerData
import uz.coder.muslimcalendar.data.network.modelDTO.PrayerResponse

interface ApiServicePrayerTime {
    //BASE_URL
    @GET("v1/calendar/{year}/{month}")
    suspend fun oneMonth(@Path("year") year:Int, @Path("month") month:Int, @Query("latitude") latitude: Double, @Query("longitude") longitude: Double, @Query("method") method:Int = 2):PrayerResponse<List<PrayerData>>
}