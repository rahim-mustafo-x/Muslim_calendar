package uz.coder.muslimcalendar.domain.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asStateFlow
import uz.coder.muslimcalendar.domain.model.AzanSound
import uz.coder.muslimcalendar.domain.model.PrayerAdjustment
import uz.coder.muslimcalendar.domain.model.PrayerStatistics

interface SettingsRepository {
    fun getPrayerAdjustments(): Flow<PrayerAdjustment>
    suspend fun savePrayerAdjustment(adjustment: PrayerAdjustment)
    fun getAzanSound(prayerName: String): Flow<AzanSound>
    suspend fun setAzanSound(prayerName: String, sound: AzanSound)
    fun getPrayerStatistics(): Flow<PrayerStatistics>
    suspend fun updatePrayerStatistics(stats: PrayerStatistics)
    suspend fun markPrayerCompleted(prayerName: String, onTime: Boolean)
    suspend fun exportSettings(): String
    suspend fun importSettings(json: String): Boolean
    suspend fun resetAllSettings()
}
