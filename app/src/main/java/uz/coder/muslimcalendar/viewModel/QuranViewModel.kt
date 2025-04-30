package uz.coder.muslimcalendar.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uz.coder.muslimcalendar.R
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
        viewModelScope.launch {
            _state.emit(QuranState.Loading)
            if (application.isConnected()){
                repo.getSurah().collect {
                    if (it.isEmpty()){
                        repo.loadQuranArab()
                        loadQuran()
                    }else{
                        _state.emit(QuranState.Success(it))
                    }
                }
            }else{
                _state.emit(QuranState.Error(application.getString(R.string.no_internet)))
            }
        }
    }
}
