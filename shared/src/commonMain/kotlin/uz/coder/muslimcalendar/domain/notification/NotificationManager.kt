package uz.coder.muslimcalendar.domain.notification

import uz.coder.muslimcalendar.domain.model.MuslimCalendar

interface NotificationManager {
    fun schedulePrayerNotifications(calendar: MuslimCalendar)
    fun cancelAllNotifications()
    fun areNotificationsEnabled(): Boolean
}
