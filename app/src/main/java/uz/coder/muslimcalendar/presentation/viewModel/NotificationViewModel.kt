package uz.coder.muslimcalendar.presentation.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import uz.coder.muslimcalendar.R
import uz.coder.muslimcalendar.SharedPref
import uz.coder.muslimcalendar.data.service.PrayerAlarmWorker
import uz.coder.muslimcalendar.domain.model.Notification
import uz.coder.muslimcalendar.domain.usecase.OneMonthDayUseCase
import uz.coder.muslimcalendar.todo.KEY_ASR
import uz.coder.muslimcalendar.todo.KEY_BOMDOD
import uz.coder.muslimcalendar.todo.KEY_PESHIN
import uz.coder.muslimcalendar.todo.KEY_QUYOSH
import uz.coder.muslimcalendar.todo.KEY_SHOM
import uz.coder.muslimcalendar.todo.KEY_XUFTON
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val oneMonthDayUseCase: OneMonthDayUseCase,
    private val sharedPref: SharedPref
) : ViewModel() {

    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications

    init {
        loadNotifications()
        restartAlarms()
    }

    /* ================= LOAD LIST ================= */

    private fun loadNotifications() {
        val names = listOf(
            context.getString(R.string.bomdod),
            context.getString(R.string.quyoshChiqishi),
            context.getString(R.string.peshin),
            context.getString(R.string.asr),
            context.getString(R.string.shom),
            context.getString(R.string.xufton)
        )

        val icons = listOf(
            sharedPref.getInt(KEY_BOMDOD, R.drawable.ic_speaker_on),
            sharedPref.getInt(KEY_QUYOSH, R.drawable.ic_bell),
            sharedPref.getInt(KEY_PESHIN, R.drawable.ic_speaker_on),
            sharedPref.getInt(KEY_ASR, R.drawable.ic_speaker_on),
            sharedPref.getInt(KEY_SHOM, R.drawable.ic_speaker_on),
            sharedPref.getInt(KEY_XUFTON, R.drawable.ic_speaker_on)
        )

        _notifications.value = names.mapIndexed { index, name ->
            Notification(
                name,
                icons[index]
            )
        }
    }

    /* ================= ICON UPDATE ================= */

    fun updateIcon(index: Int, resId: Int) {
        val key = when (index) {
            0 -> KEY_BOMDOD
            1 -> KEY_QUYOSH
            2 -> KEY_PESHIN
            3 -> KEY_ASR
            4 -> KEY_SHOM
            5 -> KEY_XUFTON
            else -> return
        }

        sharedPref.saveValue(key, resId)

        _notifications.value = _notifications.value.mapIndexed { i, item ->
            if (i == index) item.copy(icon = resId) else item
        }

        restartAlarms()
    }

    /* ================= ALARM ================= */

    private fun restartAlarms() {
        val request = OneTimeWorkRequestBuilder<PrayerAlarmWorker>().build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork(
                "PRAYER_ALARM_WORK",
                ExistingWorkPolicy.REPLACE,
                request
            )
    }

    fun oneMonthDay() = oneMonthDayUseCase()
}