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
import uz.coder.muslimcalendar.todo.isConnected
import uz.coder.muslimcalendar.todo.toSuraAyah
import uz.coder.muslimcalendar.viewModel.state.SurahState

class SurahViewModel(private val application: Application): AndroidViewModel(application) {
    private val repo = CalendarRepositoryImpl(application)
    private val _state = MutableStateFlow<SurahState>(SurahState.Init)
    val state = _state.asStateFlow()
    private val _audioPath = MutableStateFlow<String>("")
    val audioPath = _audioPath.asStateFlow()
    private val _downloadProgress = MutableStateFlow<Int>(0)
    val downloadProgress = _downloadProgress.asStateFlow()
    fun getSura(surahNumber: Int) {
        Log.d(TAG, "getSura: tushdi")
        viewModelScope.launch {
            _state.emit(SurahState.Loading)
                repo.getSurahById(surahNumber.toString()).collect {
                    getAudioPath(surahNumber.toString())
                    Log.d(TAG, "getSura: $it")
                    if (it.isEmpty()){
                        if (application.isConnected()) {
                            repo.getSura(surahNumber).collect {
                                _state.emit(SurahState.Success(it.result.toSuraAyah()))
                            }
                        }else {
                                _state.emit(SurahState.Error(application.getString(R.string.no_internet)))
                        }
                    }else{
                        _state.emit(SurahState.Success(it))
                    }
                }
            }
    }
    fun getNameOfSura(surahNumber: Int) = repo.getSuraByNumber(surahNumber)
    fun getAudioPath(sura:String) {
        viewModelScope.launch {
            repo.getAudioPath(sura).collect {
                if (it.path!=null){
                    Log.d(TAG, "getAudioPath: ${it.path}")
                    _audioPath.emit(it.path)
                }else{
                    if (application.isConnected()){
                        _audioPath.emit(getQuranAudioUrl(sura.toInt()))
                    }
                }
            }
        }
    }
    fun downloadSurah(suraAyahs: List<SurahList>, url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.downloadSurah(suraAyahs, url).collect {
                repo.observeProgress(it).collect { progress ->
                    _downloadProgress.value = progress
                }
            }

        }
    }

    private fun getQuranAudioUrl(number:Int):String {
        val numberOfSurah = "%03d".format(number)
        return "https://server16.mp3quran.net/a_binaoun/Rewayat-Hafs-A-n-Assem/${numberOfSurah}.mp3"
    }
}
private const val TAG = "SurahViewModel"