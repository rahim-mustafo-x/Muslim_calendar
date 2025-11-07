package uz.coder.muslimcalendar.presentation.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uz.coder.muslimcalendar.R
import uz.coder.muslimcalendar.domain.model.quran.Sura
import uz.coder.muslimcalendar.domain.usecase.GetSurahUseCase
import uz.coder.muslimcalendar.domain.usecase.LoadQuranArabUseCase
import uz.coder.muslimcalendar.presentation.viewModel.state.QuranState
import uz.coder.muslimcalendar.todo.isConnected
import javax.inject.Inject

@HiltViewModel
class QuranViewModel @Inject constructor(
    private val application: Application,
    private val getSurahUseCase: GetSurahUseCase,
    private val loadQuranArabUseCase: LoadQuranArabUseCase
) : AndroidViewModel(application) {
    private val _state = MutableStateFlow<QuranState>(QuranState.Init)
    val state = _state.asStateFlow()
    init {
        loadQuran()
    }
    fun loadQuran() {
        viewModelScope.launch {
            _state.value = QuranState.Loading

            getSurahUseCase().collect { suraList ->
                if (suraList.isEmpty()) {
                    if (application.isConnected()) {
                        loadQuranArabUseCase()
                    } else {
                        _state.value = QuranState.Error(application.getString(R.string.no_internet))
                    }
                } else {
                    _state.value = QuranState.Success(suraList)
                }
            }
        }
    }
    fun searchSura(text: String, suraList: List<Sura>) = suraList.filter {
        it.englishName.lowercase().contains(text.lowercase().trim()) ||
        it.englishNameTranslation.lowercase().contains(text.lowercase().trim())
    }
}

