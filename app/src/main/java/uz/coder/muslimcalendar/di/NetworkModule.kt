package uz.coder.muslimcalendar.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import uz.coder.muslimcalendar.data.network.KtorApiService
import uz.coder.muslimcalendar.data.network.KtorClient
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    @Named("prayerTime")
    fun providePrayerTimeClient(): HttpClient = KtorClient.prayerTimeClient

    @Provides
    @Singleton
    @Named("quranArab")
    fun provideQuranArabClient(): HttpClient = KtorClient.quranArabClient

    @Provides
    @Singleton
    @Named("quranUzbek")
    fun provideQuranUzbekClient(): HttpClient = KtorClient.quranUzbekClient

    @Provides
    @Singleton
    @Named("download")
    fun provideDownloadClient(): HttpClient = KtorClient.downloadClient

    @Provides
    @Singleton
    fun provideKtorApiService(
        @Named("prayerTime") prayerTimeClient: HttpClient,
        @Named("quranArab") quranArabClient: HttpClient,
        @Named("quranUzbek") quranUzbekClient: HttpClient
    ): KtorApiService = KtorApiService(prayerTimeClient, quranArabClient, quranUzbekClient)
}