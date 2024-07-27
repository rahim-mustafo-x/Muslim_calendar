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
import uz.coder.muslimcalendar.models.model.Date
import uz.coder.muslimcalendar.models.model.Item
import uz.coder.muslimcalendar.models.model.Time
import uz.coder.muslimcalendar.repository.CalendarRepositoryImpl
import uz.coder.muslimcalendar.todo.ASR
import uz.coder.muslimcalendar.todo.BOMDOD
import uz.coder.muslimcalendar.todo.ICON_ASR
import uz.coder.muslimcalendar.todo.ICON_BOMDOD
import uz.coder.muslimcalendar.todo.ICON_PESHIN
import uz.coder.muslimcalendar.todo.ICON_QUYOSH
import uz.coder.muslimcalendar.todo.ICON_SHOM
import uz.coder.muslimcalendar.todo.ICON_XUFTON
import uz.coder.muslimcalendar.todo.PESHIN
import uz.coder.muslimcalendar.todo.REGION
import uz.coder.muslimcalendar.todo.SHOM
import uz.coder.muslimcalendar.todo.SOUND_ASR
import uz.coder.muslimcalendar.todo.SOUND_BOMDOD
import uz.coder.muslimcalendar.todo.SOUND_PESHIN
import uz.coder.muslimcalendar.todo.SOUND_QUYOSH
import uz.coder.muslimcalendar.todo.SOUND_SHOM
import uz.coder.muslimcalendar.todo.SOUND_XUFTON
import uz.coder.muslimcalendar.todo.VITR
import uz.coder.muslimcalendar.todo.XUFTON
import uz.coder.muslimcalendar.todo.isConnected

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
        fromPreferencesQazo()
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
    }
    fun setPeshin(peshin: Int){
        viewModelScope.launch {
            _peshin.emit(peshin)
        }
    }
    fun setAsr(asr: Int){
        viewModelScope.launch {
            _asr.emit(asr)
        }
    }
    fun setShom(shom: Int){
        viewModelScope.launch {
            _shom.emit(shom)
        }
    }
    fun setXufton(xufton: Int){
        viewModelScope.launch {
            _xufton.emit(xufton)
        }
    }
    fun setVitr(vitr: Int){
        viewModelScope.launch {
            _vitr.emit(vitr)
        }
    }

    fun fromPreferencesQazo(){
        viewModelScope.launch {
            _bomdod.emit(
                getInt(BOMDOD))
            _peshin.emit(
                getInt(PESHIN))
            _asr.emit(
                getInt(ASR))
            _shom.emit(
                getInt(SHOM))
            _xufton.emit(
                getInt(XUFTON))
            _vitr.emit(
                getInt(VITR))

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

    fun timeList() = flow {
        repo.presentDay().collect{
            emit(listOf(
                Time(Item(application.getString(R.string.bomdod), it.tongSaharlik),
                    it.tongSaharlik.subSequence(0,2).toString().toInt(),
                    it.tongSaharlik.subSequence(3, it.tongSaharlik.length).toString().toInt(), getInt(ICON_BOMDOD, R.drawable.sound_off), getInt(
                    SOUND_BOMDOD, R.raw.nothing)),

                Time(Item(application.getString(R.string.quyosh), it.quyosh),
                    it.quyosh.subSequence(0,2).toString().toInt(),
                    it.quyosh.subSequence(3, it.quyosh.length).toString().toInt(), getInt(ICON_QUYOSH, R.drawable.bell_off), getInt(
                    SOUND_QUYOSH, R.raw.nothing)),

                Time(Item(application.getString(R.string.peshin), it.peshin),
                    it.peshin.subSequence(0,2).toString().toInt(),
                    it.peshin.subSequence(3, it.peshin.length).toString().toInt(), getInt(ICON_PESHIN, R.drawable.sound_off), getInt(
                    SOUND_PESHIN, R.raw.nothing)),

                Time(Item(application.getString(R.string.asr), it.asr),
                    it.asr.subSequence(0,2).toString().toInt(),
                    it.asr.subSequence(3, it.asr.length).toString().toInt(), getInt(ICON_ASR, R.drawable.sound_off), getInt(
                    SOUND_ASR, R.raw.nothing)),

                Time(Item(application.getString(R.string.shom), it.shomIftor),
                    it.shomIftor.subSequence(0,2).toString().toInt(),
                    it.shomIftor.subSequence(3, it.shomIftor.length).toString().toInt(), getInt(ICON_SHOM, R.drawable.sound_off), getInt(
                    SOUND_SHOM, R.raw.nothing)),

                Time(Item(application.getString(R.string.xufton), it.hufton),
                    it.hufton.subSequence(0,2).toString().toInt(),
                    it.hufton.subSequence(3, it.hufton.length).toString().toInt(), getInt(ICON_XUFTON, R.drawable.sound_off), getInt(
                    SOUND_XUFTON, R.raw.nothing))))
        }
    }

    fun itemList() = flow{
        repo.presentDay().collect{
            emit(listOf(Item(application.getString(R.string.bomdod), it.tongSaharlik),
                    Item(application.getString(R.string.quyosh), it.quyosh),
                    Item(application.getString(R.string.peshin), it.peshin),
            Item(application.getString(R.string.asr), it.asr),
            Item(application.getString(R.string.shom), it.shomIftor),
            Item(application.getString(R.string.xufton), it.hufton)))
        }
    }

    fun day() = flow {
        repo.presentDay().collect{
            emit(Date(it.day, it.month-1, it.weekday, it.hijriDay+1, it.hijriMonth))
        }
    }

    fun saveInt(key:String, value:Int){
        preferences.edit().putInt(key,value).apply()
    }
    private fun getInt(key:String, defValue:Int = 0) = preferences.getInt(key, defValue)
}
