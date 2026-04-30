package uz.coder.muslimcalendar.domain.repository

interface NotificationScheduler {
    suspend fun scheduleAllAlarms()
    suspend fun rescheduleAll()
    suspend fun scheduleDailyNotification()
    suspend fun scheduleFollowUpReminder()
}
