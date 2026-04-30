package uz.coder.muslimcalendar.data.repository

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import uz.coder.muslimcalendar.SharedPref
import uz.coder.muslimcalendar.domain.model.AzanSound
import uz.coder.muslimcalendar.domain.model.PrayerAdjustment
import uz.coder.muslimcalendar.domain.model.PrayerStatistics
import uz.coder.muslimcalendar.domain.repository.NotificationScheduler
import uz.coder.muslimcalendar.domain.repository.SettingsRepository
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Serializable
data class SettingsExport(
    val adjustments: PrayerAdjustment,
    val statistics: PrayerStatistics,
    val theme: String,
    val azanSounds: Map<String, Int>
)

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sharedPref: SharedPref,
    private val json: Json,
    private val scheduler: Provider<NotificationScheduler>
): SettingsRepository {
    private val _prayerAdjustments = MutableStateFlow(loadPrayerAdjustments())
    private val _prayerStatistics = MutableStateFlow(loadPrayerStatistics())

    override fun getPrayerAdjustments(): Flow<PrayerAdjustment> = _prayerAdjustments.asStateFlow()

    override suspend fun savePrayerAdjustment(adjustment: PrayerAdjustment) {
        sharedPref.saveValue("adj_bomdod", adjustment.bomdod)
        sharedPref.saveValue("adj_quyosh", adjustment.quyosh)
        sharedPref.saveValue("adj_peshin", adjustment.peshin)
        sharedPref.saveValue("adj_asr", adjustment.asr)
        sharedPref.saveValue("adj_shom", adjustment.shom)
        sharedPref.saveValue("adj_xufton", adjustment.xufton)
        _prayerAdjustments.value = adjustment
        scheduler.get().rescheduleAll()
    }

    override fun getAzanSound(prayerName: String): Flow<AzanSound> {
        val resourceId = sharedPref.getInt("azan_sound_$prayerName", AzanSound.DEFAULT.resourceId)
        return MutableStateFlow(AzanSound.fromResourceId(resourceId)).asStateFlow()
    }

    override suspend fun setAzanSound(prayerName: String, sound: AzanSound) {
        sharedPref.saveValue("azan_sound_$prayerName", sound.resourceId)
    }

    override fun getPrayerStatistics(): Flow<PrayerStatistics> = _prayerStatistics.asStateFlow()

    override suspend fun updatePrayerStatistics(stats: PrayerStatistics) {
        sharedPref.saveValue("stats_total", stats.totalPrayers)
        sharedPref.saveValue("stats_on_time", stats.prayedOnTime)
        sharedPref.saveValue("stats_current_streak", stats.currentStreak)
        sharedPref.saveValue("stats_longest_streak", stats.longestStreak)
        sharedPref.saveValue("stats_last_prayer", stats.lastPrayerDate)
        sharedPref.saveValue("stats_qazo_bomdod", stats.bomdodQazo)
        sharedPref.saveValue("stats_qazo_peshin", stats.peshinQazo)
        sharedPref.saveValue("stats_qazo_asr", stats.asrQazo)
        sharedPref.saveValue("stats_qazo_shom", stats.shomQazo)
        sharedPref.saveValue("stats_qazo_xufton", stats.xuftonQazo)
        sharedPref.saveValue("stats_today_bomdod", stats.bomdodToday)
        sharedPref.saveValue("stats_today_peshin", stats.peshinToday)
        sharedPref.saveValue("stats_today_asr", stats.asrToday)
        sharedPref.saveValue("stats_today_shom", stats.shomToday)
        sharedPref.saveValue("stats_today_xufton", stats.xuftonToday)
        _prayerStatistics.value = stats
    }

    override suspend fun markPrayerCompleted(prayerName: String, onTime: Boolean) {
        val current = _prayerStatistics.value
        val now = System.currentTimeMillis()
        
        val newStats = when (prayerName.lowercase()) {
            "bomdod" -> current.copy(bomdodToday = true)
            "peshin" -> current.copy(peshinToday = true)
            "asr" -> current.copy(asrToday = true)
            "shom" -> current.copy(shomToday = true)
            "xufton" -> current.copy(xuftonToday = true)
            else -> current
        }.let {
            it.copy(
                totalPrayers = it.totalPrayers + 1,
                prayedOnTime = if (onTime) it.prayedOnTime + 1 else it.prayedOnTime,
                lastPrayerDate = now
            )
        }
        
        updatePrayerStatistics(newStats)
    }

    override suspend fun checkAndResetDailyPrayers() {
        val current = _prayerStatistics.value
        val now = System.currentTimeMillis()
        
        // Check if it's a new day
        val lastDate = java.util.Calendar.getInstance().apply { timeInMillis = current.lastPrayerDate }
        val today = java.util.Calendar.getInstance()
        
        if (lastDate.get(java.util.Calendar.DAY_OF_YEAR) != today.get(java.util.Calendar.DAY_OF_YEAR) ||
            lastDate.get(java.util.Calendar.YEAR) != today.get(java.util.Calendar.YEAR)) {
            
            // Increment Qazo if not prayed yesterday
            var newBomdodQazo = current.bomdodQazo
            var newPeshinQazo = current.peshinQazo
            var newAsrQazo = current.asrQazo
            var newShomQazo = current.shomQazo
            var newXuftonQazo = current.xuftonQazo
            
            if (!current.bomdodToday) newBomdodQazo++
            if (!current.peshinToday) newPeshinQazo++
            if (!current.asrToday) newAsrQazo++
            if (!current.shomToday) newShomQazo++
            if (!current.xuftonToday) newXuftonQazo++
            
            val resetStats = current.copy(
                bomdodToday = false,
                peshinToday = false,
                asrToday = false,
                shomToday = false,
                xuftonToday = false,
                bomdodQazo = newBomdodQazo,
                peshinQazo = newPeshinQazo,
                asrQazo = newAsrQazo,
                shomQazo = newShomQazo,
                xuftonQazo = newXuftonQazo,
                lastPrayerDate = now
            )
            updatePrayerStatistics(resetStats)
        }
    }

    override suspend fun exportSettings(): String {
        val settings = SettingsExport(
            adjustments = loadPrayerAdjustments(),
            statistics = loadPrayerStatistics(),
            theme = sharedPref.getString("theme_mode", "SYSTEM"),
            azanSounds = mapOf(
                "bomdod" to sharedPref.getInt("azan_sound_bomdod", AzanSound.DEFAULT.resourceId),
                "peshin" to sharedPref.getInt("azan_sound_peshin", AzanSound.DEFAULT.resourceId),
                "asr" to sharedPref.getInt("azan_sound_asr", AzanSound.DEFAULT.resourceId),
                "shom" to sharedPref.getInt("azan_sound_shom", AzanSound.DEFAULT.resourceId),
                "xufton" to sharedPref.getInt("azan_sound_xufton", AzanSound.DEFAULT.resourceId)
            )
        )
        return json.encodeToString(settings)
    }

    override suspend fun importSettings(json: String): Boolean {
        return try {
            val settings = this.json.decodeFromString<SettingsExport>(json)
            savePrayerAdjustment(settings.adjustments)
            updatePrayerStatistics(settings.statistics)
            sharedPref.saveValue("theme_mode", settings.theme)
            settings.azanSounds.forEach { (prayer, soundId) ->
                sharedPref.saveValue("azan_sound_$prayer", soundId)
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun resetAllSettings() {
        savePrayerAdjustment(PrayerAdjustment())
        updatePrayerStatistics(PrayerStatistics())
        setDailyNotificationEnabled(true)
        setDailyNotificationTime("00:00")
        setFollowUpReminderEnabled(true)
        setFollowUpReminderDelay(15)
    }

    override fun isDailyNotificationEnabled(): Flow<Boolean> = 
        _prayerStatistics.map { sharedPref.getBoolean("daily_notif_enabled", true) }

    override suspend fun setDailyNotificationEnabled(enabled: Boolean) {
        sharedPref.saveValue("daily_notif_enabled", enabled)
        _prayerStatistics.value = _prayerStatistics.value // Trigger flow update
        scheduler.get().rescheduleAll()
    }

    override fun getDailyNotificationTime(): Flow<String> = 
        _prayerStatistics.map { sharedPref.getString("daily_notif_time", "00:00") }

    override suspend fun setDailyNotificationTime(time: String) {
        sharedPref.saveValue("daily_notif_time", time)
        _prayerStatistics.value = _prayerStatistics.value // Trigger flow update
        scheduler.get().rescheduleAll()
    }

    override fun isFollowUpReminderEnabled(): Flow<Boolean> = 
        _prayerStatistics.map { sharedPref.getBoolean("follow_up_enabled", true) }

    override suspend fun setFollowUpReminderEnabled(enabled: Boolean) {
        sharedPref.saveValue("follow_up_enabled", enabled)
        _prayerStatistics.value = _prayerStatistics.value // Trigger flow update
        scheduler.get().rescheduleAll()
    }

    override fun getFollowUpReminderDelay(): Flow<Int> = 
        _prayerStatistics.map { sharedPref.getInt("follow_up_delay", 15) }

    override suspend fun setFollowUpReminderDelay(minutes: Int) {
        sharedPref.saveValue("follow_up_delay", minutes)
        _prayerStatistics.value = _prayerStatistics.value // Trigger flow update
        scheduler.get().rescheduleAll()
    }

    override fun getNextNotificationTime(): Flow<String> = _prayerStatistics.map {
        val enabled = sharedPref.getBoolean("daily_notif_enabled", true)
        if (!enabled) return@map "Disabled"
        
        val time = sharedPref.getString("daily_notif_time", "00:00")
        "Next update at $time"
    }

    private fun loadPrayerAdjustments(): PrayerAdjustment {
        return PrayerAdjustment(
            bomdod = sharedPref.getInt("adj_bomdod", 0),
            quyosh = sharedPref.getInt("adj_quyosh", 0),
            peshin = sharedPref.getInt("adj_peshin", 0),
            asr = sharedPref.getInt("adj_asr", 0),
            shom = sharedPref.getInt("adj_shom", 0),
            xufton = sharedPref.getInt("adj_xufton", 0)
        )
    }

    private fun loadPrayerStatistics(): PrayerStatistics {
        return PrayerStatistics(
            totalPrayers = sharedPref.getInt("stats_total", 0),
            prayedOnTime = sharedPref.getInt("stats_on_time", 0),
            currentStreak = sharedPref.getInt("stats_current_streak", 0),
            longestStreak = sharedPref.getInt("stats_longest_streak", 0),
            lastPrayerDate = sharedPref.getLong("stats_last_prayer", 0L),
            bomdodQazo = sharedPref.getInt("stats_qazo_bomdod", 0),
            peshinQazo = sharedPref.getInt("stats_qazo_peshin", 0),
            asrQazo = sharedPref.getInt("stats_qazo_asr", 0),
            shomQazo = sharedPref.getInt("stats_qazo_shom", 0),
            xuftonQazo = sharedPref.getInt("stats_qazo_xufton", 0),
            bomdodToday = sharedPref.getBoolean("stats_today_bomdod", false),
            peshinToday = sharedPref.getBoolean("stats_today_peshin", false),
            asrToday = sharedPref.getBoolean("stats_today_asr", false),
            shomToday = sharedPref.getBoolean("stats_today_shom", false),
            xuftonToday = sharedPref.getBoolean("stats_today_xufton", false)
        )
    }
}
