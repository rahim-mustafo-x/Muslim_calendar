package uz.coder.muslimcalendar.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uz.coder.muslimcalendar.data.repository.NotificationSchedulerImpl
import uz.coder.muslimcalendar.domain.repository.NotificationScheduler
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NotificationModule {

    @Binds
    @Singleton
    abstract fun bindNotificationScheduler(
        impl: NotificationSchedulerImpl
    ): NotificationScheduler
}
