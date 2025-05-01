package uz.coder.muslimcalendar.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uz.coder.muslimcalendar.R
import uz.coder.muslimcalendar.repository.CalendarRepositoryImpl
import uz.coder.muslimcalendar.todo.UZB
import uz.coder.muslimcalendar.todo.cyrillicToLatin
import uz.coder.muslimcalendar.todo.isConnected
import uz.coder.muslimcalendar.todo.appLanguage
import uz.coder.muslimcalendar.viewModel.state.SurahState

class SurahViewModel(private val application: Application): AndroidViewModel(application) {
    private val repo = CalendarRepositoryImpl(application)
    private val _state = MutableStateFlow<SurahState>(SurahState.Init)
    val state = _state.asStateFlow()
    fun getSura(surahNumber: Int) {
        viewModelScope.launch {
            _state.emit(SurahState.Loading)
            if (application.isConnected()){
                repo.getSura(surahNumber).collect {surah->
                    repo.getSurahByNumber(surahNumber).collect {
                        if (appLanguage != UZB){
                            _state.emit(SurahState.Success(surah.result, it.englishName))
                        }
                        _state.emit(SurahState.Success(surah.result.map {
                            val translation = async(Dispatchers.Default) { it.translation.cyrillicToLatin() }
                            val footnotes = async(Dispatchers.Default) { it.footnotes.cyrillicToLatin() }
                            it.copy(translation = translation.await(), footnotes = footnotes.await())
                        }, it.englishName))
                    }
                }
            }else {
                _state.emit(SurahState.Error(application.getString(R.string.no_internet)))
            }
        }
    }
    fun getQuranAudioUrl(number:Int):String {
        val numberOfSurah = "%03d".format(number)
        return "https://server16.mp3quran.net/a_binaoun/Rewayat-Hafs-A-n-Assem/${numberOfSurah}.mp3"
    }
}