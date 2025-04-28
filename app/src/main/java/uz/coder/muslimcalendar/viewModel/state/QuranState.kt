package uz.coder.muslimcalendar.viewModel.state

import uz.coder.muslimcalendar.models.model.quran.Sura

sealed class QuranState {
    data object Init : QuranState()
    object Loading : QuranState()
    data class Success(val data: List<Sura>) : QuranState()
    data class Error(val message: String) : QuranState()
}