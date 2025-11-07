package uz.coder.muslimcalendar.di

import android.content.Context
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import uz.coder.muslimcalendar.data.db.AppDatabase
import uz.coder.muslimcalendar.data.network.ApiClient
import uz.coder.muslimcalendar.data.network.ApiServicePrayerTime
import uz.coder.muslimcalendar.data.network.ApiServiceQuranArab
import uz.coder.muslimcalendar.data.network.ApiServiceQuranUzbek
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppProvidesModule {
    @Provides
    @Singleton
    fun providesAppDatabase(@ApplicationContext context: Context): AppDatabase{
        return AppDatabase.instance(context)
    }
    @Provides
    @Singleton
    fun providesPrayerTime(): ApiServicePrayerTime{
        return ApiClient.getPrayerTime().create(ApiServicePrayerTime::class.java)
    }
    @Provides
    @Singleton
    fun providesQuranArab(): ApiServiceQuranArab{
        return ApiClient.getQuranArab().create(ApiServiceQuranArab::class.java)
    }
    @Provides
    @Singleton
    fun providesQuranUzbek(): ApiServiceQuranUzbek{
        return ApiClient.getQuranUzbek().create(ApiServiceQuranUzbek::class.java)
    }
    @Provides
    fun provideGson() = Gson()
}