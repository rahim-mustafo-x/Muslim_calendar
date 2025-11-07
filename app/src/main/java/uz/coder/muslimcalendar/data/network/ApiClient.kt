package uz.coder.muslimcalendar.data.network

import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    private val gson = Gson()
    init {
        System.loadLibrary("native-lib")
    }

    @JvmStatic
    private external fun getPrayerTimeUrl():String
    @JvmStatic
    private external fun getQuranUzbekUrl():String

    @JvmStatic
    private external fun getQuranArabUrl():String

    val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    fun getPrayerTime(): Retrofit{
        val client = OkHttpClient.Builder().apply {
            writeTimeout(2L, TimeUnit.MINUTES)
            connectTimeout(2L, TimeUnit.MINUTES)
            readTimeout(2L, TimeUnit.MINUTES)
            addInterceptor(logging)
        }.build()
        return Retrofit.Builder()
            .baseUrl(getPrayerTimeUrl())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()
    }
    fun getQuranArab(): Retrofit{
        val client = OkHttpClient.Builder().apply {
            writeTimeout(2L, TimeUnit.MINUTES)
            connectTimeout(2L, TimeUnit.MINUTES)
            readTimeout(2L, TimeUnit.MINUTES)
            addInterceptor(logging)
        }.build()
        return Retrofit.Builder()
            .baseUrl(getQuranArabUrl())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()
    }
    fun getQuranUzbek(): Retrofit{
        val client = OkHttpClient.Builder().apply {
            writeTimeout(2L, TimeUnit.MINUTES)
            connectTimeout(2L, TimeUnit.MINUTES)
            readTimeout(2L, TimeUnit.MINUTES)
            addInterceptor(logging)
        }.build()
        return Retrofit.Builder()
            .baseUrl(getQuranUzbekUrl())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()
    }
}