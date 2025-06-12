package uz.coder.muslimcalendar.viewModel

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import uz.coder.muslimcalendar.R
import uz.coder.muslimcalendar.models.model.Notification
import uz.coder.muslimcalendar.receiver.AlarmBroadCast
import uz.coder.muslimcalendar.repository.CalendarRepositoryImpl
import uz.coder.muslimcalendar.todo.SharedPref
import uz.coder.muslimcalendar.viewModel.state.NotificationState
import java.util.Calendar

class NotificationViewModel(
    private val application: Application
) : AndroidViewModel(application) {
    init {
        setAlarm()
    }
    private val repository = CalendarRepositoryImpl(application)
    private val sharedPref = SharedPref.getInstance(application.applicationContext)

    private val iconFlows = listOf(
        MutableStateFlow(sharedPref.getInt(KEY_BOMDOD, R.drawable.ic_speaker_on)),
        MutableStateFlow(sharedPref.getInt(KEY_QUYOSH, R.drawable.ic_bell)),
        MutableStateFlow(sharedPref.getInt(KEY_PESHIN, R.drawable.ic_speaker_on)),
        MutableStateFlow(sharedPref.getInt(KEY_ASR, R.drawable.ic_speaker_on)),
        MutableStateFlow(sharedPref.getInt(KEY_SHOM, R.drawable.ic_speaker_on)),
        MutableStateFlow(sharedPref.getInt(KEY_XUFTON, R.drawable.ic_speaker_on))
    )

    private val _state = MutableStateFlow<NotificationState>(NotificationState.Loading)
    val state: StateFlow<NotificationState> = _state

    private var currentNames: List<String> = emptyList()

    fun loadNotifications(names: List<String>) {
        require(names.size >= iconFlows.size) { "Kamida 6 ta nom yuboring" }
        currentNames = names.take(iconFlows.size)
        refreshState()
    }

    fun updateIcon(index: Int, resId: Int) {
        if (index !in iconFlows.indices) return

        when (index) {
            0 -> sharedPref.saveValue(KEY_BOMDOD, resId)
            1 -> sharedPref.saveValue(KEY_QUYOSH, resId)
            2 -> sharedPref.saveValue(KEY_PESHIN, resId)
            3 -> sharedPref.saveValue(KEY_ASR, resId)
            4 -> sharedPref.saveValue(KEY_SHOM, resId)
            5 -> sharedPref.saveValue(KEY_XUFTON, resId)
        }

        iconFlows[index].value = resId
        refreshState()
        setAlarm()
    }

    private fun refreshState() {
        if (currentNames.isEmpty()) return
        val list = iconFlows.mapIndexed { i, flow ->
            Notification(currentNames[i], flow.value)
        }
        viewModelScope.launch {
            repository.oneMonth().collect {
                _state.value = NotificationState.Success(list, it)
            }
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    fun setAlarm() {
        Log.d(TAG, "setAlarm: tushdi notificationga")
        viewModelScope.launch {
            try{
                repository.oneMonth().collect {list->
                    val alarmManager = application.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    for (item in list){
                    val intent = Intent(application, AlarmBroadCast::class.java)
                    val pendingIntent = PendingIntent.getBroadcast(application, item.day, intent, PendingIntent.FLAG_IMMUTABLE)
                    alarmManager.cancel(pendingIntent)
                }
                    for (item in list){
                    val listOfTime = listOf(
                        Pair(item.tongSaharlik, sharedPref.getInt(KEY_BOMDOD, -1)),
                        Pair(item.sunRise, sharedPref.getInt(KEY_QUYOSH, -1)),
                        Pair(item.peshin, sharedPref.getInt(KEY_PESHIN, -1)),
                        Pair(item.asr, sharedPref.getInt(KEY_ASR, -1)),
                        Pair(item.shomIftor, sharedPref.getInt(KEY_SHOM, -1)),
                        Pair(item.hufton, sharedPref.getInt(KEY_XUFTON, -1))
                    )
                    listOfTime.forEach{
                        Log.d(TAG, "setAlarm: ${it.first}")
                        val split = it.first.split(":")
                        val hour = split[0].toInt()
                        val minute = split[1].toInt()
                        Log.d(TAG, "setAlarm: hour: $hour minute: $minute")
                        val calendar = Calendar.getInstance().apply {
                        set(Calendar.MONTH, item.month)
                            set(Calendar.DAY_OF_MONTH, item.day)
                            set(Calendar.HOUR_OF_DAY, hour)
                            set(Calendar.MINUTE, minute)
                            set(Calendar.SECOND, 0)
                        }
                        val music = when (it.second) {
                            R.drawable.ic_speaker_on -> R.raw.azan
                            R.drawable.ic_bell -> -2
                            R.drawable.ic_speaker_cross -> -1
                            else -> -1
                        }
                        val intent = AlarmBroadCast.getIntent(application, hour, minute, application.getString(R.string.prayingTimeCome), music)
                        val pendingIntent = PendingIntent.getBroadcast(application, item.day, intent, PendingIntent.FLAG_IMMUTABLE)
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
                    }
                }
            }
            }catch (_:Exception){
                delay(500)
                setAlarm()
            }
        }
    }



    companion object {
        private const val KEY_BOMDOD = "bomdod"
        private const val KEY_QUYOSH = "quyoshChiqishi"
        private const val KEY_PESHIN = "peshin"
        private const val KEY_ASR = "asr"
        private const val KEY_SHOM = "shom"
        private const val KEY_XUFTON = "xufton"
        private const val TAG = "NotificationViewModel"
    }
}