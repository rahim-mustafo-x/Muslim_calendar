package uz.coder.muslimcalendar.data.network.modelDTO

data class PrayerResponse<T>(
    val code: Int? = null,
    val status: String? = null,
    val data: T? = null
)