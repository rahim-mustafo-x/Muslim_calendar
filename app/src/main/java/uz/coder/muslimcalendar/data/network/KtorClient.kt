package uz.coder.muslimcalendar.data.network

import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object KtorClient {
    init {
        System.loadLibrary("native-lib")
    }



    @JvmStatic
    external fun getPrayerTimeUrl(): String

    @JvmStatic
    external fun getQuranUzbekUrl(): String

    @JvmStatic
    external fun getQuranArabUrl(): String

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
        }
    }
}
