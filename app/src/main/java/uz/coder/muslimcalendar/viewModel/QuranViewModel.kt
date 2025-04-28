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
import uz.coder.muslimcalendar.viewModel.state.SurahState

class QuranViewModel(private val application: Application) : AndroidViewModel(application) {
    private val repo = CalendarRepositoryImpl(application)
    private val _surahList = MutableStateFlow<QuranState>(QuranState.Init)
    val surahList = _surahList.asStateFlow()
    private val _surah = MutableStateFlow<SurahState>(SurahState.Init)
    val surah = _surah.asStateFlow()
    init {
        loadQuran()
    }
    fun loadQuran() {
        viewModelScope.launch {
            _surahList.emit(QuranState.Loading)
            if (application.isConnected()){
                repo.getSurah().collect {
                    if (it.isEmpty()){
                        repo.loadQuranArab()
                        loadQuran()
                    }else{
                        _surahList.emit(QuranState.Success(it))
                    }
                }
            }else{
                _surahList.emit(QuranState.Error(application.getString(R.string.no_internet)))
            }
        }
    }
    fun getSura(surahNumber: Int) {
        viewModelScope.launch {
            _surah.emit(SurahState.Loading)
            if (application.isConnected()){
                repo.getSura(surahNumber).collect {surah->
                    repo.getSurahByNumber(surahNumber).collect {
                        _surah.emit(SurahState.Success(surah.result, it.englishName))
                    }
                }
            }else {
                _surah.emit(SurahState.Error(application.getString(R.string.no_internet)))
            }
        }
    }

    fun getQuranAudioUrl(number:Int):String {
        val numberOfSurah = "%03d".format(number)
        return "https://server16.mp3quran.net/a_binaoun/Rewayat-Hafs-A-n-Assem/${numberOfSurah}.mp3"
    }
}
