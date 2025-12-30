package uz.coder.muslimcalendar.presentation.viewModel

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.core.app.ActivityCompat
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import uz.coder.muslimcalendar.R
import uz.coder.muslimcalendar.domain.model.Calendar
import uz.coder.muslimcalendar.domain.usecase.LoadingUseCase
import uz.coder.muslimcalendar.domain.usecase.OneMonthDayUseCase
import uz.coder.muslimcalendar.domain.usecase.PresentDayUseCase
import uz.coder.muslimcalendar.presentation.ui.theme.Light_Blue
import uz.coder.muslimcalendar.todo.ALL_TASBEH
import uz.coder.muslimcalendar.todo.ASR
import uz.coder.muslimcalendar.todo.BOMDOD
import uz.coder.muslimcalendar.todo.PESHIN
import uz.coder.muslimcalendar.todo.SHOM
import uz.coder.muslimcalendar.todo.TASBEH
import uz.coder.muslimcalendar.todo.VITR
import uz.coder.muslimcalendar.todo.XUFTON
import uz.coder.muslimcalendar.todo.isConnected
import javax.inject.Inject

@HiltViewModel
data class CalendarViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val oneMonthDayUseCase: OneMonthDayUseCase,
    private val presentDayUseCase: PresentDayUseCase,
    private val loadingUseCase: LoadingUseCase
    ): ViewModel(){
    private val fusedLocationClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }
    private var latitude = 0.0
    private var longitude = 0.0
    private val preferences by lazy { context.getSharedPreferences(context.getString(R.string.app_name), Application.MODE_PRIVATE) }
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
    private val _allTasbeh = MutableStateFlow(0)
    val allTasbeh = _allTasbeh.asStateFlow()
    private val _tasbeh = MutableStateFlow(0)
    val tasbeh = _tasbeh.asStateFlow()
    init {
        init()
        loading()
    }
    private fun init() {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener {
                latitude = it.latitude
                longitude = it.longitude
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
    fun fromPreferencesTasbeh(){
        viewModelScope.launch {
            _tasbeh.emit(getInt(TASBEH))
            _allTasbeh.emit(getInt(ALL_TASBEH))
        }
    }

    private fun loadInformationFromInternet() {
        viewModelScope.launch {
            Log.d("TAG", "loadInformationFromInternet: ")
            if (context.isConnected()){
                loadingUseCase(longitude, latitude)
            }
        }
    }

    fun saveTasbeh(tasbeh:Int){
        viewModelScope.launch {
            _tasbeh.emit(tasbeh)
        }
    }
    fun saveAllTasbeh(allTasbeh:Int){
        viewModelScope.launch {
            _allTasbeh.emit(allTasbeh)
        }
    }
    fun refreshTasbehAndAllTasbeh(){
        viewModelScope.launch {
            Log.d("TAG", "refreshTasbehAndAllTasbeh: ")
            _allTasbeh.emit(0)
            _tasbeh.emit(0)
        }
    }
    fun oneMonth() = channelFlow<List<Calendar>> {
        oneMonthDayUseCase().collect{ it ->
            send(mutableListOf(Calendar(context.getString(R.string.dayMonth), White, Light_Blue), Calendar(context.getString(R.string.bomdod), White, Light_Blue), Calendar(context.getString(R.string.quyoshChiqishi), White, Light_Blue), Calendar(context.getString(R.string.peshin), White, Light_Blue), Calendar(context.getString(R.string.asr), White, Light_Blue), Calendar(context.getString(R.string.shom), White, Light_Blue), Calendar(context.getString(R.string.xufton), White, Light_Blue)).apply {
                it.forEach {
                    add(Calendar(it.day.toString().plus("-${context.resources.getStringArray(R.array.months)[it.month-1]}"), White, Light_Blue))
                    add(Calendar(it.tongSaharlik, Black, White))
                    add(Calendar(it.sunRise, Black, White))
                    add(Calendar(it.peshin, Black, White))
                    add(Calendar(it.asr, Black, White))
                    add(Calendar(it.shomIftor, Black, White))
                    add(Calendar(it.hufton, Black, White))
                }
            })
        }
    }

    fun saveInt(key:String, value:Int){
        preferences.edit { putInt(key, value) }
    }
    private fun getInt(key:String, defValue:Int = 0) = preferences.getInt(key, defValue)
}
