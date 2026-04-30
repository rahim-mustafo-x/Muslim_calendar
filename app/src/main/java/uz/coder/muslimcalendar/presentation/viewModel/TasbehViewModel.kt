package uz.coder.muslimcalendar.presentation.viewModel

import android.app.Application
import android.content.Context
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import uz.coder.muslimcalendar.R
import uz.coder.muslimcalendar.todo.*
import javax.inject.Inject

@HiltViewModel
class TasbehViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val preferences by lazy { context.getSharedPreferences(context.getString(R.string.app_name), Application.MODE_PRIVATE) }

    private val _tasbeh = MutableStateFlow(getInt(TASBEH))
    val tasbeh = _tasbeh.asStateFlow()

    private val _allTasbeh = MutableStateFlow(getInt(ALL_TASBEH))
    val allTasbeh = _allTasbeh.asStateFlow()

    fun saveTasbeh(value: Int) { _tasbeh.value = value; saveInt(TASBEH, value) }
    fun saveAllTasbeh(value: Int) { _allTasbeh.value = value; saveInt(ALL_TASBEH, value) }

    fun reset() {
        _tasbeh.value = 0
        _allTasbeh.value = 0
        saveInt(TASBEH, 0)
        saveInt(ALL_TASBEH, 0)
    }

    private fun saveInt(key: String, value: Int) {
        preferences.edit { putInt(key, value) }
    }

    private fun getInt(key: String, defValue: Int = 0) = preferences.getInt(key, defValue)
}
