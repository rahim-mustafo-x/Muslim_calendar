package uz.coder.muslimcalendar.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class PrayerAdjustment(
    val bomdod: Int = 0,      // Minutes to adjust
    val quyosh: Int = 0,
    val peshin: Int = 0,
    val asr: Int = 0,
    val shom: Int = 0,
    val xufton: Int = 0
)

enum class AzanSound(val displayName: String, val resourceId: Int) {
    DEFAULT("Standart azan", uz.coder.muslimcalendar.R.raw.azan),
    MAKKAH("Makka azani", -1),  // Will be added
    MADINAH("Madina azani", -1),
    EGYPT("Misr azani", -1),
    NOTIFICATION("Oddiy bildirishnoma", -2);
    
    companion object {
        fun fromResourceId(id: Int): AzanSound {
            return entries.find { it.resourceId == id } ?: DEFAULT
        }
    }
}

@Serializable
data class PrayerStatistics(
    val totalPrayers: Int = 0,
    val prayedOnTime: Int = 0,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val lastPrayerDate: Long = 0L,
    val bomdodQazo: Int = 0,
    val peshinQazo: Int = 0,
    val asrQazo: Int = 0,
    val shomQazo: Int = 0,
    val xuftonQazo: Int = 0,
    val bomdodToday: Boolean = false,
    val peshinToday: Boolean = false,
    val asrToday: Boolean = false,
    val shomToday: Boolean = false,
    val xuftonToday: Boolean = false
)
