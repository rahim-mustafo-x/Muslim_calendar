package uz.coder.muslimcalendar.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import uz.coder.muslimcalendar.R
import uz.coder.muslimcalendar.model.model.Item
import uz.coder.muslimcalendar.model.model.MuslimCalendar
import uz.coder.muslimcalendar.repository.CalendarRepositoryImpl
import uz.coder.muslimcalendar.todo.REGION
import uz.coder.muslimcalendar.todo.isConnected

data class CalendarViewModel(private val application: Application):AndroidViewModel(application){
    private val repo = CalendarRepositoryImpl(application)
    private val preferences by lazy { application.getSharedPreferences(application.getString(R.string.app_name), Application.MODE_PRIVATE) }
    private val _calendar = MutableStateFlow(MuslimCalendar())
    val calendar = _calendar.asStateFlow()
    init {
        loading()
    }
    fun loading(){
        try {
            loadInformationFromInternet()
        }catch (_:Exception){}
        try {
            putInformation()
        }catch (_:Exception){}
    }

    private fun putInformation() {
        viewModelScope.launch {
             repo.presentDay().collectLatest {
                 Log.d("TAG", "putInformation : $it")
                 _calendar.emit(it)
             }
        }
    }

    private fun loadInformationFromInternet() {
        viewModelScope.launch {
            Log.d("TAG", "loadInformationFromInternet: ")
            if (application.isConnected()){
                remove()
                repo.loading()
            }
        }
    }
    private fun remove(){
        viewModelScope.launch {
            repo.remove()
        }
    }

    fun region(region:String){
        preferences.edit().putString(REGION, region).apply()
    }

    fun timeList() = flow{
        repo.presentDay().collect{
            emit(mutableListOf<Item>().apply {
                add(Item(application.getString(R.string.bomdod), it.tongSaharlik))
                add(Item(application.getString(R.string.quyosh), it.quyosh))
                add(Item(application.getString(R.string.peshin), it.peshin))
                add(Item(application.getString(R.string.asr), it.asr))
                add(Item(application.getString(R.string.shom), it.shomIftor))
                add(Item(application.getString(R.string.xufton), it.hufton))
            })
        }
    }
}
