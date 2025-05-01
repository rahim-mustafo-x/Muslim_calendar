package uz.coder.muslimcalendar.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import uz.coder.muslimcalendar.todo.appLanguage

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    fun selectLanguage(language: String) {
        appLanguage = language
        Log.d(TAG, "selectLanguage: $language")
    }
}

private const val TAG = "SettingsViewModel"