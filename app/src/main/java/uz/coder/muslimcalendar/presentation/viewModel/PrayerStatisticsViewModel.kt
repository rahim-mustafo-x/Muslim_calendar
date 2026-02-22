package uz.coder.muslimcalendar.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import uz.coder.muslimcalendar.domain.model.PrayerStatistics
import uz.coder.muslimcalendar.domain.repository.SettingsRepository
import javax.inject.Inject

@HiltViewModel
class PrayerStatisticsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    
    val statistics = settingsRepository.getPrayerStatistics()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            PrayerStatistics()
        )
    
    fun markPrayerCompleted(prayerName: String, onTime: Boolean) {
        viewModelScope.launch {
            settingsRepository.markPrayerCompleted(prayerName, onTime)
        }
    }
    
    fun resetStatistics() {
        viewModelScope.launch {
            settingsRepository.updatePrayerStatistics(PrayerStatistics())
        }
    }
}
