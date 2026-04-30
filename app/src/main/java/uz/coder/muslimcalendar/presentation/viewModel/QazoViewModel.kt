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
class QazoViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val preferences by lazy { context.getSharedPreferences(context.getString(R.string.app_name), Application.MODE_PRIVATE) }

    private val _bomdod = MutableStateFlow(getInt(BOMDOD))
    val bomdod = _bomdod.asStateFlow()

    private val _peshin = MutableStateFlow(getInt(PESHIN))
    val peshin = _peshin.asStateFlow()

    private val _asr = MutableStateFlow(getInt(ASR))
    val asr = _asr.asStateFlow()

    private val _shom = MutableStateFlow(getInt(SHOM))
    val shom = _shom.asStateFlow()

    private val _xufton = MutableStateFlow(getInt(XUFTON))
    val xufton = _xufton.asStateFlow()

    private val _vitr = MutableStateFlow(getInt(VITR))
    val vitr = _vitr.asStateFlow()

    fun setBomdod(value: Int) { _bomdod.value = value; saveInt(BOMDOD, value) }
    fun setPeshin(value: Int) { _peshin.value = value; saveInt(PESHIN, value) }
    fun setAsr(value: Int) { _asr.value = value; saveInt(ASR, value) }
    fun setShom(value: Int) { _shom.value = value; saveInt(SHOM, value) }
    fun setXufton(value: Int) { _xufton.value = value; saveInt(XUFTON, value) }
    fun setVitr(value: Int) { _vitr.value = value; saveInt(VITR, value) }

    private fun saveInt(key: String, value: Int) {
        preferences.edit { putInt(key, value) }
    }

    private fun getInt(key: String, defValue: Int = 0) = preferences.getInt(key, defValue)
}
