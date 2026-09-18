package uz.coder.muslimcalendar.shared.domain.notification

import uz.coder.muslimcalendar.shared.domain.model.MuslimCalendar

interface NotificationManager {
    fun schedulePrayerNotifications(calendar: MuslimCalendar)
    fun cancelAllNotifications()
    fun areNotificationsEnabled(): Boolean
}
