package uz.coder.muslimcalendar.presentation.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uz.coder.muslimcalendar.R
import uz.coder.muslimcalendar.domain.model.quran.SurahList
import uz.coder.muslimcalendar.domain.usecase.DownloadSurahUseCase
import uz.coder.muslimcalendar.domain.usecase.GetAudioPathUseCase
import uz.coder.muslimcalendar.domain.usecase.GetSuraUseCase
import uz.coder.muslimcalendar.domain.usecase.GetSurahByIdUseCase
import uz.coder.muslimcalendar.domain.usecase.GetSurahByNumberUseCase
import uz.coder.muslimcalendar.presentation.viewModel.state.SurahState
import uz.coder.muslimcalendar.todo.isConnected
import uz.coder.muslimcalendar.todo.toSuraAyah
import javax.inject.Inject

@HiltViewModel
class SurahViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getSurahByIdUseCase: GetSurahByIdUseCase,
    private val getSuraUseCase: GetSuraUseCase,
    private val getSurahByNumberUseCase: GetSurahByNumberUseCase,
    private val audioPathUseCase: GetAudioPathUseCase,
    private val downloadSurahUseCase: DownloadSurahUseCase
): ViewModel() {
    private val _state = MutableStateFlow<SurahState>(SurahState.Init)
    val state = _state.asStateFlow()
    private val _audioPath = MutableStateFlow("")
    val audioPath = _audioPath.asStateFlow()
    fun getSura(surahNumber: Int) {
        Log.d(TAG, "getSura: tushdi")
        viewModelScope.launch {
            _state.emit(SurahState.Loading)
                getSurahByIdUseCase(surahNumber.toString()).collect { it ->
                    getAudioPath(surahNumber.toString())
                    Log.d(TAG, "getSura: $it")
                    if (it.isEmpty()){
                        if (context.isConnected()) {
                            getSuraUseCase(surahNumber).collect {
                                _state.emit(SurahState.Success(it.result.toSuraAyah()))
                            }
                        }else {
                                _state.emit(SurahState.Error(context.getString(R.string.no_internet)))
                        }
                    }else{
                        _state.emit(SurahState.Success(it))
                    }
                }
            }
    }
    fun getNameOfSura(surahNumber: Int) = getSurahByNumberUseCase(surahNumber)
    fun getAudioPath(sura:String) {
        viewModelScope.launch {
            audioPathUseCase(sura).collect {
                if (it.path!=null){
                    Log.d(TAG, "getAudioPath: ${it.path}")
                    _audioPath.emit(it.path)
                }else{
                    if (context.isConnected()){
                        _audioPath.emit(getQuranAudioUrl(sura.toInt()))
                    }
                }
            }
        }
    }
    fun downloadSurah(suraAyahs: List<SurahList>, url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            downloadSurahUseCase(suraAyahs, url)
        }
    }

    private fun getQuranAudioUrl(number:Int):String {
        val numberOfSurah = "%03d".format(number)
        return "https://server16.mp3quran.net/a_binaoun/Rewayat-Hafs-A-n-Assem/${numberOfSurah}.mp3"
    }
}
private const val TAG = "SurahViewModel"