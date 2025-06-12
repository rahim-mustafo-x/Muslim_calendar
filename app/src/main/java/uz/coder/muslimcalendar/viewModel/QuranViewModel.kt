package uz.coder.muslimcalendar.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import uz.coder.muslimcalendar.R
import uz.coder.muslimcalendar.models.model.quran.Sura
import uz.coder.muslimcalendar.repository.CalendarRepositoryImpl
import uz.coder.muslimcalendar.todo.isConnected
import uz.coder.muslimcalendar.viewModel.state.QuranState

class QuranViewModel(private val application: Application) : AndroidViewModel(application) {
    private val repo = CalendarRepositoryImpl(application)
    private val _state = MutableStateFlow<QuranState>(QuranState.Init)
    val state = _state.asStateFlow()
    init {
        loadQuran()
    }
    fun loadQuran() {
        viewModelScope.launch(Dispatchers.IO) {
            _state.emit(QuranState.Loading)
                repo.getSurah().collect {
                    if (it.isEmpty()){
                        if (application.isConnected()){
                            repo.loadQuranArab()
                            loadQuran()
                        }else{
                        _state.value = QuranState.Error(application.getString(R.string.no_internet))
                        }
                    }else{
                        _state.value = QuranState.Success(it)
                    }
                }
        }
    }

    fun searchSura(text: String, suraList: List<Sura>) = suraList.filter {
        it.englishName.lowercase().contains(text.lowercase().trim()) ||
        it.englishNameTranslation.lowercase().contains(text.lowercase().trim())
    }
}
