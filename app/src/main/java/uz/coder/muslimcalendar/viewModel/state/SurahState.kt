package uz.coder.muslimcalendar.viewModel.state

import uz.coder.muslimcalendar.models.model.quran.SurahList

sealed class SurahState {
    data object Init : SurahState()
    object Loading : SurahState()
    data class Success(val data: List<SurahList>, val nameOfSurah: String) : SurahState()
    data class Error(val message: String) : SurahState()
}