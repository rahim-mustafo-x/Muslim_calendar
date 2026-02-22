package uz.coder.muslimcalendar.presentation.viewModel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import uz.coder.muslimcalendar.presentation.ui.theme.ThemeManager
import uz.coder.muslimcalendar.presentation.ui.theme.ThemeMode
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val themeManager: ThemeManager
) : ViewModel() {
    
    val themeMode: StateFlow<ThemeMode> = themeManager.themeMode
    
    fun setThemeMode(mode: ThemeMode) {
        themeManager.setThemeMode(mode)
    }
    
    fun toggleTheme() {
        themeManager.toggleTheme()
    }
}
