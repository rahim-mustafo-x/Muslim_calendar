package uz.coder.muslimcalendar.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uz.coder.muslimcalendar.R
import uz.coder.muslimcalendar.models.model.quran.SurahList
import uz.coder.muslimcalendar.repository.CalendarRepositoryImpl
import uz.coder.muslimcalendar.todo.getQuranAudioUrl
import uz.coder.muslimcalendar.todo.isConnected
import uz.coder.muslimcalendar.todo.toSuraAyah
import uz.coder.muslimcalendar.viewModel.state.SurahState

class SurahViewModel(private val application: Application): AndroidViewModel(application) {
    private val repo = CalendarRepositoryImpl(application)
    private val _state = MutableStateFlow<SurahState>(SurahState.Init)
    val state = _state.asStateFlow()
    private val _audioPath = MutableStateFlow<String>("")
    val audioPath = _audioPath.asStateFlow()
    fun getSura(surahNumber: Int, audioPath:String) {
        Log.d(TAG, "getSura: tushdi")
        viewModelScope.launch {
            _state.emit(SurahState.Loading)
                repo.getSurahById(surahNumber.toString()).collect {
                    Log.d(TAG, "getSura: $it")
                    if (it.isEmpty()){
                        if (application.isConnected()) {
                            repo.getSura(surahNumber, audioPath).collect {
                                _state.emit(SurahState.Success(it.result.toSuraAyah()))
                                _audioPath.emit(getQuranAudioUrl(surahNumber))
                                Log.d(TAG, "getSura: ${getQuranAudioUrl(surahNumber)}")
                            }
                        }else {
                                _state.emit(SurahState.Error(application.getString(R.string.no_internet)))
                        }
                    }else{
                        _audioPath.emit(it.first().audioPath)
                        _state.emit(SurahState.Success(it))
                    }
                }
            }
        }
    fun downloadSurah(suraAyahs: List<SurahList>, url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.downloadSurah(suraAyahs, url)
        }
    }
}
private const val TAG = "SurahViewModel"