package uz.coder.muslimcalendar.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import uz.coder.muslimcalendar.domain.model.AzanSound
import uz.coder.muslimcalendar.domain.model.PrayerAdjustment
import uz.coder.muslimcalendar.domain.repository.SettingsRepository
import javax.inject.Inject

@HiltViewModel
class AdvancedSettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    
    val prayerAdjustments = settingsRepository.getPrayerAdjustments()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            PrayerAdjustment()
        )
    
    fun savePrayerAdjustments(adjustments: PrayerAdjustment) {
        viewModelScope.launch {
            settingsRepository.savePrayerAdjustment(adjustments)
        }
    }
    
    fun setAzanSound(prayerName: String, sound: AzanSound) {
        viewModelScope.launch {
            settingsRepository.setAzanSound(prayerName, sound)
        }
    }
    
    suspend fun exportSettings(): String {
        return settingsRepository.exportSettings()
    }
    
    suspend fun importSettings(json: String): Boolean {
        return settingsRepository.importSettings(json)
    }
    
    suspend fun resetAllSettings() {
        settingsRepository.resetAllSettings()
    }
}
