package uz.coder.muslimcalendar.data.network

import android.R.string
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json


object KtorClient {
//    init {
//        System.loadLibrary("native-lib")
//    }


    fun getPrayerTimeUrl(): String {
        return "https://api.aladhan.com/"
    }

    fun getQuranUzbekUrl(): String {
        return "https://quranenc.com/"
    }

    fun getQuranArabUrl(): String {
        return "https://api.alquran.cloud/"
    }

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
        prettyPrint = true
    }

    fun createClient(baseUrl: String): HttpClient {
        return HttpClient(Android) {
            install(ContentNegotiation) {
                json(json)
            }

            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.BODY
            }

            install(HttpTimeout) {
                requestTimeoutMillis = 120_000
                connectTimeoutMillis = 120_000
                socketTimeoutMillis = 120_000
            }

            defaultRequest {
                url(baseUrl)
                header("User-Agent", "Mozilla/5.0 (Linux; Android 13; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36")
                header("Accept", "application/json")
            }
        }
    }

    val prayerTimeClient: HttpClient by lazy {
        createClient(getPrayerTimeUrl())
    }

    val quranArabClient: HttpClient by lazy {
        createClient(getQuranArabUrl())
    }

    val quranUzbekClient: HttpClient by lazy {
        createClient(getQuranUzbekUrl())
    }

    val downloadClient: HttpClient by lazy {
        HttpClient(Android) {
            install(HttpTimeout) {
                requestTimeoutMillis = 300_000
                connectTimeoutMillis = 120_000
                socketTimeoutMillis = 300_000
            }
            defaultRequest {
                header("User-Agent", "Mozilla/5.0 (Linux; Android 13; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36")
            }
        }
    }
}
