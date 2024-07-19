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
import uz.coder.muslimcalendar.models.model.Item
import uz.coder.muslimcalendar.repository.CalendarRepositoryImpl
import uz.coder.muslimcalendar.todo.ASR
import uz.coder.muslimcalendar.todo.BOMDOD
import uz.coder.muslimcalendar.todo.PESHIN
import uz.coder.muslimcalendar.todo.REGION
import uz.coder.muslimcalendar.todo.SHOM
import uz.coder.muslimcalendar.todo.VITR
import uz.coder.muslimcalendar.todo.XUFTON
import uz.coder.muslimcalendar.todo.isConnected
import uz.coder.muslimcalendar.todo.workReceiver

data class CalendarViewModel(private val application: Application):AndroidViewModel(application){
    private val repo = CalendarRepositoryImpl(application)
    private val preferences by lazy { application.getSharedPreferences(application.getString(R.string.app_name), Application.MODE_PRIVATE) }
    private val _bomdod = MutableStateFlow(0)
    val bomdod = _bomdod.asStateFlow()
    private val _peshin = MutableStateFlow(0)
    val peshin = _peshin.asStateFlow()
    private val _asr = MutableStateFlow(0)
    val asr = _asr.asStateFlow()
    private val _shom = MutableStateFlow(0)
    val shom = _shom.asStateFlow()
    private val _xufton = MutableStateFlow(0)
    val xufton = _xufton.asStateFlow()
    private val _vitr = MutableStateFlow(0)
    val vitr = _vitr.asStateFlow()
    init {
        loading()
        showNotification()
        fromPreferencesQazo()
    }

    private fun showNotification() {
        viewModelScope.launch {
            timeList().collectLatest {
                it.forEach { item->
                    workReceiver(item)
                }
            }
        }
    }

    fun loading(){
        try {
            loadInformationFromInternet()
        }catch (_:Exception){}
    }

    fun setBomdod(bomdod: Int){
        viewModelScope.launch {
            _bomdod.emit(bomdod)
        }
    }fun setPeshin(peshin: Int){
        viewModelScope.launch {
            _peshin.emit(peshin)
        }
    }fun setAsr(asr: Int){
        viewModelScope.launch {
            _asr.emit(asr)
        }
    }fun setShom(shom: Int){
        viewModelScope.launch {
            _shom.emit(shom)
        }
    }fun setXufton(xufton: Int){
        viewModelScope.launch {
            _xufton.emit(xufton)
        }
    }fun setVitr(vitr: Int){
        viewModelScope.launch {
            _vitr.emit(vitr)
        }
    }

    fun fromPreferencesQazo(){
        viewModelScope.launch {
            _bomdod.emit(
                getTime(BOMDOD))
            _peshin.emit(
                getTime(PESHIN))
            _asr.emit(
                getTime(ASR))
            _shom.emit(
                getTime(SHOM))
            _xufton.emit(
                getTime(XUFTON))
            _vitr.emit(
                getTime(VITR))

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
    fun saveTime(key:String, value:Int){
        preferences.edit().putInt(key,value).apply()
    }
    private fun getTime(key:String) = preferences.getInt(key, 0)
}
