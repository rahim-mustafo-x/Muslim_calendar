package uz.coder.muslimcalendar.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uz.coder.muslimcalendar.data.repository.CalendarRepositoryImpl
import uz.coder.muslimcalendar.domain.repository.CalendarRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppBindsModule {
    @Singleton
    @Binds
    abstract fun bindCalendarRepository(impl: CalendarRepositoryImpl): CalendarRepository
}