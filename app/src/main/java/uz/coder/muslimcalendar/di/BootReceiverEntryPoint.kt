package uz.coder.muslimcalendar.di

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uz.coder.muslimcalendar.domain.repository.NotificationScheduler

@EntryPoint
@InstallIn(SingletonComponent::class)
interface BootReceiverEntryPoint {
    fun getNotificationScheduler(): NotificationScheduler
}