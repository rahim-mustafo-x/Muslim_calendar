package uz.coder.muslimcalendar.presentation.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
    @ApplicationContext private val context: Context,
    private val getSurahUseCase: GetSurahUseCase,
    private val loadQuranArabUseCase: LoadQuranArabUseCase
) : ViewModel() {
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
                    if (context.isConnected()) {
                        loadQuranArabUseCase()
                    } else {
                        _state.value = QuranState.Error(context.getString(R.string.no_internet))
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

