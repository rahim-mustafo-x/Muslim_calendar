package uz.coder.muslimcalendar.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import uz.coder.muslimcalendar.R
import uz.coder.muslimcalendar.models.model.Date
import uz.coder.muslimcalendar.models.model.Item
import uz.coder.muslimcalendar.repository.CalendarRepositoryImpl
import uz.coder.muslimcalendar.todo.isConnected
import uz.coder.muslimcalendar.viewModel.state.HomeState

class HomeViewModel(private val application: Application):AndroidViewModel(application) {
    private val repo = CalendarRepositoryImpl(application)
    private val _state = MutableStateFlow<HomeState>(HomeState.Init)
    val state = _state.asStateFlow()
    init {
        remove()
        loading()
    }
    private fun remove(){
        viewModelScope.launch {
            repo.remove()
        }
    }
    fun loading(){
        try {
            loadInformationFromInternet()
        }catch (_:Exception){}
    }
    private fun loadInformationFromInternet() {
        viewModelScope.launch {
            Log.d("TAG", "loadInformationFromInternet: ")
            if (application.isConnected()){
                _state.value = HomeState.Loading
                repo.loading()
            }else{
                _state.value = HomeState.Error(application.getString(R.string.no_internet))
            }
        }
    }
    fun itemList() {
        viewModelScope.launch {
            _state.value = HomeState.Loading
            repo.presentDay().collect{
                _state.value = HomeState.Success(listOf(
                    Item(application.getString(R.string.bomdod), it.tongSaharlik),
                    Item(application.getString(R.string.quyosh), it.quyosh),
                    Item(application.getString(R.string.peshin), it.peshin),
                    Item(application.getString(R.string.asr), it.asr),
                    Item(application.getString(R.string.shom), it.shomIftor),
                    Item(application.getString(R.string.xufton), it.hufton)))
            }
        }
    }

    fun day() = flow {
        repo.presentDay().collect{
            emit(Date(it.day, it.month-1, it.weekday, it.hijriDay, it.hijriMonth, it.region))
        }
    }


}