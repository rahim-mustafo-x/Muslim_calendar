package uz.coder.muslimcalendar.presentation.viewModel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.StateFlow
import uz.coder.muslimcalendar.presentation.ui.theme.ThemeManager
import uz.coder.muslimcalendar.presentation.ui.theme.ThemeMode

class SettingsViewModel(
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
