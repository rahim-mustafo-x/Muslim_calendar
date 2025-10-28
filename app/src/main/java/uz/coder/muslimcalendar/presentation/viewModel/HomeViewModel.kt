package uz.coder.muslimcalendar.presentation.viewModel

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import uz.coder.muslimcalendar.R
import uz.coder.muslimcalendar.domain.model.Date
import uz.coder.muslimcalendar.data.repository.CalendarRepositoryImpl
import uz.coder.muslimcalendar.todo.isConnected
import uz.coder.muslimcalendar.presentation.viewModel.state.HomeState

class HomeViewModel(private val application: Application):AndroidViewModel(application) {
    private val repo = CalendarRepositoryImpl(application)
    private val _state = MutableStateFlow<HomeState>(HomeState.Init)
    val state = _state.asStateFlow()
    private var latitude = 0.0
    private var longitude = 0.0
    private val fusedLocationClient by lazy {
        LocationServices.getFusedLocationProviderClient(application)
    }
    init {
        initLocation()
        remove()
        loadInfo()
    }

    fun loadInfo() {
        if (application.isConnected()){
            loadInformationFromInternet(latitude, longitude)
        }
    }

    @SuppressLint("MissingPermission")
    private fun initLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener {
            latitude = it.latitude
            longitude = it.longitude
        }
    }

    private fun remove(){
        if (application.isConnected()){
            viewModelScope.launch {
                repo.remove()
            }
        }
    }
    fun loadInformationFromInternet(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            Log.d("TAG", "loadInformationFromInternet: ")
            if (application.isConnected()){
                _state.value = HomeState.Loading
                repo.loading(longitude, latitude)
            }else{
                _state.value = HomeState.Error(application.getString(R.string.no_internet))
            }
        }
    }
    fun itemList() {
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = HomeState.Loading
            repo.presentDay().collect{
                _state.value = HomeState.Success(it)
            }
        }
    }

    fun day() = flow {
        repo.presentDay().collect{
            emit(Date(it.day, it.month-1, it.weekday, it.hijriDay, it.hijriMonth))
        }
    }


}