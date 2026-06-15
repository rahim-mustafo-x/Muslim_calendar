package uz.coder.muslimcalendar.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class NotificationSettings(
    val prayerNotificationsEnabled: Boolean = true,
    val newHijriDayEnabled: Boolean = false,
    val jumuahReminderEnabled: Boolean = true,
    val ramadanReminderEnabled: Boolean = true,
    val islamicEventsEnabled: Boolean = true,
    val prayerOffsets: Map<String, Int> = emptyMap() // name to offset in minutes
)
