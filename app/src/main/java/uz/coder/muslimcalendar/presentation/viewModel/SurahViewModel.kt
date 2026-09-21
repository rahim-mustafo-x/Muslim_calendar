package uz.coder.muslimcalendar.presentation.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import uz.coder.muslimcalendar.R
import uz.coder.muslimcalendar.data.service.QuranPlayerManager
import uz.coder.muslimcalendar.domain.model.quran.SurahList
import uz.coder.muslimcalendar.domain.usecase.DownloadSurahUseCase
import uz.coder.muslimcalendar.domain.usecase.GetAudioPathUseCase
import uz.coder.muslimcalendar.domain.usecase.GetSuraUseCase
import uz.coder.muslimcalendar.domain.usecase.GetSurahByIdUseCase
import uz.coder.muslimcalendar.domain.usecase.GetSurahByNumberUseCase
import uz.coder.muslimcalendar.presentation.viewModel.state.SurahState
import uz.coder.muslimcalendar.todo.isConnected
import uz.coder.muslimcalendar.todo.toSuraAyah

class SurahViewModel(
    private val application: Application,
    private val getSurahByIdUseCase: GetSurahByIdUseCase,
    private val getSuraUseCase: GetSuraUseCase,
    private val getSurahByNumberUseCase: GetSurahByNumberUseCase,
    private val audioPathUseCase: GetAudioPathUseCase,
    private val downloadSurahUseCase: DownloadSurahUseCase,
    val quranPlayerManager: QuranPlayerManager
) : ViewModel() {
    private val context get() = application.applicationContext

    private val _state = MutableStateFlow<SurahState>(SurahState.Init)
    val state = _state.asStateFlow()

    private val _audioPath = MutableStateFlow("")
    val audioPath = _audioPath.asStateFlow()

    companion object {
        private const val TAG = "SurahViewModel"
    }

    fun getSura(surahNumber: Int) {
        Log.d(TAG, "getSura: $surahNumber")
        viewModelScope.launch {
            _state.emit(SurahState.Loading)
            getAudioPath(surahNumber.toString())

            try {
                val localAyahs = getSurahByIdUseCase(surahNumber.toString()).first()
                Log.d(TAG, "getSura local DB count: ${localAyahs.size}")

                if (localAyahs.isNotEmpty()) {
                    _state.emit(SurahState.Success(localAyahs))
                } else {
                    if (context.isConnected()) {
                        val surah = getSuraUseCase(surahNumber).first()
                        val ayahs = surah.result.toSuraAyah()

                        if (ayahs.isNotEmpty()) {
                            _state.emit(SurahState.Success(ayahs))
                        } else {
                            _state.emit(SurahState.Error("Ma'lumot topilmadi"))
                        }
                    } else {
                        _state.emit(SurahState.Error(context.getString(R.string.no_internet)))
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "getSura error", e)
                _state.emit(SurahState.Error(e.localizedMessage ?: "Ma'lumot yuklab bo'lmadi"))
            }
        }
    }

    fun getNameOfSura(surahNumber: Int) = getSurahByNumberUseCase(surahNumber)

    fun getAudioPath(sura: String) {
        viewModelScope.launch {
            val audioModel = audioPathUseCase(sura).firstOrNull()
            if (!audioModel?.path.isNullOrEmpty()) {
                Log.d(TAG, "getAudioPath local: ${audioModel.path}")
                _audioPath.emit(audioModel.path)
            } else if (context.isConnected()) {
                val remoteUrl = getQuranAudioUrl(sura.toIntOrNull() ?: 1)
                Log.d(TAG, "getAudioPath remote: $remoteUrl")
                _audioPath.emit(remoteUrl)
            }
        }
    }

    fun downloadSurah(suraAyahs: List<SurahList>, url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            downloadSurahUseCase(suraAyahs, url)
        }
    }

    private fun getQuranAudioUrl(number: Int): String {
        val numberOfSurah = "%03d".format(number)
        return "https://server16.mp3quran.net/a_binaoun/Rewayat-Hafs-A-n-Assem/${numberOfSurah}.mp3"
    }
}