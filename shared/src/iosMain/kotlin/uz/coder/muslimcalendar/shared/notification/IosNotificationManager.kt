package uz.coder.muslimcalendar.shared.notification

import uz.coder.muslimcalendar.shared.domain.model.MuslimCalendar
import uz.coder.muslimcalendar.shared.domain.notification.NotificationManager
import platform.UserNotifications.*
import platform.Foundation.*

class IosNotificationManager : NotificationManager {
    
    override fun schedulePrayerNotifications(calendar: MuslimCalendar) {
        val center = UNUserNotificationCenter.currentNotificationCenter()
        
        val prayerTimes = listOf(
            calendar.tongSaharlik to "Bomdod",
            calendar.sunRise to "Quyosh",
            calendar.peshin to "Peshin",
            calendar.asr to "Asr",
            calendar.shomIftor to "Shom",
            calendar.hufton to "Xufton"
        )

        prayerTimes.forEachIndexed { index, (timeStr, name) ->
            if (timeStr.isEmpty()) return@forEachIndexed
            val parts = timeStr.split(":")
            if (parts.size < 2) return@forEachIndexed
            val h = parts[0].toIntOrNull() ?: return@forEachIndexed
            val m = parts[1].toIntOrNull() ?: return@forEachIndexed

            val dateComponents = NSDateComponents().apply {
                setYear(calendar.year.toLong())
                setMonth(calendar.month.toLong())
                setDay(calendar.day.toLong())
                setHour(h.toLong())
                setMinute(m.toLong())
            }

            val trigger = UNCalendarNotificationTrigger.triggerWithDateMatchingComponents(dateComponents, false)
            
            val content = UNMutableNotificationContent().apply {
                setTitle("$name namozi")
                setBody("Namoz vaqti bo'ldi")
                setSound(UNNotificationSound.defaultSound())
            }

            val request = UNNotificationRequest.requestWithIdentifier(
                "prayer_${calendar.year}_${calendar.month}_${calendar.day}_$index",
                content,
                trigger
            )

            center.addNotificationRequest(request) { error ->
                if (error != null) {
                    println("Error scheduling notification: ${error.localizedDescription}")
                }
            }
        }
    }

    override fun cancelAllNotifications() {
        UNUserNotificationCenter.currentNotificationCenter().removeAllPendingNotificationRequests()
    }

    override fun areNotificationsEnabled(): Boolean {
        // iOS specific check would go here (usually involving a callback/completion handler)
        return true
    }
}
