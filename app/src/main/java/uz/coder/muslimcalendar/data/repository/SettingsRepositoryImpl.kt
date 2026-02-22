package uz.coder.muslimcalendar.data.repository

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import uz.coder.muslimcalendar.SharedPref
import uz.coder.muslimcalendar.domain.model.AzanSound
import uz.coder.muslimcalendar.domain.model.PrayerAdjustment
import uz.coder.muslimcalendar.domain.model.PrayerStatistics
import uz.coder.muslimcalendar.domain.repository.SettingsRepository
import javax.inject.Inject
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
    private val json: Json
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
        _prayerStatistics.value = stats
    }

    override suspend fun markPrayerCompleted(prayerName: String, onTime: Boolean) {
        val current = _prayerStatistics.value
        val now = System.currentTimeMillis()
        val dayInMillis = 24 * 60 * 60 * 1000L
        
        val newStreak = if (now - current.lastPrayerDate < dayInMillis * 2) {
            current.currentStreak + 1
        } else {
            1
        }
        
        val newStats = current.copy(
            totalPrayers = current.totalPrayers + 1,
            prayedOnTime = if (onTime) current.prayedOnTime + 1 else current.prayedOnTime,
            currentStreak = newStreak,
            longestStreak = maxOf(current.longestStreak, newStreak),
            lastPrayerDate = now
        )
        
        updatePrayerStatistics(newStats)
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
            lastPrayerDate = sharedPref.getLong("stats_last_prayer", 0L)
        )
    }
}
