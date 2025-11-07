package uz.coder.muslimcalendar.presentation.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uz.coder.muslimcalendar.R
import uz.coder.muslimcalendar.domain.usecase.LoadingUseCase
import uz.coder.muslimcalendar.domain.usecase.PresentDayUseCase
import uz.coder.muslimcalendar.domain.usecase.RemoveUseCase
import uz.coder.muslimcalendar.presentation.viewModel.state.HomeState
import uz.coder.muslimcalendar.todo.isConnected
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val application: Application,
    private val removeUseCase: RemoveUseCase,
    private val presentDayUseCase: PresentDayUseCase,
    private val loadingUseCase: LoadingUseCase
):AndroidViewModel(application) {
    private val _state = MutableStateFlow<HomeState>(HomeState.Init)
    val state = _state.asStateFlow()
    init {
        remove()
    }

    private fun remove(){
        if (application.isConnected()){
            viewModelScope.launch {
                removeUseCase()
            }
        }
    }
    fun loadInformationFromInternet(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            Log.d("TAG", "loadInformationFromInternet: ")
            if (application.isConnected()){
                _state.value = HomeState.Loading
                loadingUseCase(longitude, latitude)
            }else{
                _state.value = HomeState.Error(application.getString(R.string.no_internet))
            }
        }
    }
    fun itemList() {
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = HomeState.Loading
            presentDayUseCase().collect{
                _state.value = HomeState.Success(it)
            }
        }
    }
}