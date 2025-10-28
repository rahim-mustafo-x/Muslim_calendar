package uz.coder.muslimcalendar.presentation.viewModel.state

import uz.coder.muslimcalendar.models.model.SuraAyah

sealed class SurahState {
    data object Init : SurahState()
    object Loading : SurahState()
    data class Success(val data: List<SuraAyah>) : SurahState()
    data class Error(val message: String) : SurahState()
}