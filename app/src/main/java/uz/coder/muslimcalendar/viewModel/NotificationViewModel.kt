package uz.coder.muslimcalendar.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import uz.coder.muslimcalendar.R
import uz.coder.muslimcalendar.models.model.Notification
import uz.coder.muslimcalendar.todo.SharedPref
import uz.coder.muslimcalendar.viewModel.state.NotificationState

class NotificationViewModel(
    application: Application
) : AndroidViewModel(application) {

    /* -------------------- SharedPref -------------------- */

    private val sharedPref = SharedPref.getInstance(application.applicationContext)

    /* -------------------- Icon holatlari -------------------- */

    private val iconFlows = listOf(
        MutableStateFlow(sharedPref.getInt(KEY_BOMDOD, R.drawable.ic_speaker_on)),
        MutableStateFlow(sharedPref.getInt(KEY_QUYOSH, R.drawable.ic_bell)),
        MutableStateFlow(sharedPref.getInt(KEY_PESHIN, R.drawable.ic_speaker_on)),
        MutableStateFlow(sharedPref.getInt(KEY_ASR, R.drawable.ic_speaker_on)),
        MutableStateFlow(sharedPref.getInt(KEY_SHOM, R.drawable.ic_speaker_on)),
        MutableStateFlow(sharedPref.getInt(KEY_XUFTON, R.drawable.ic_speaker_on))
    )

    /* -------------------- UI holati -------------------- */

    private val _state = MutableStateFlow<NotificationState>(NotificationState.Loading)
    val state: StateFlow<NotificationState> = _state

    /* -------------------- Namoz nomlari -------------------- */

    private var currentNames: List<String> = emptyList()

    /**
     * Namoz nomlarini yuklaydi va dastlabki Success holatni yuboradi.
     * names.size kamida 6 bo‘lishi shart, aks holda IllegalArgumentException.
     */
    fun loadNotifications(names: List<String>) {
        require(names.size >= iconFlows.size) { "Kamida 6 ta nom yuboring" }
        currentNames = names.take(iconFlows.size)
        refreshState()
    }

    /**
     * Tanlangan iconni yangilaydi: SharedPref + StateFlow.
     */
    fun updateIcon(index: Int, resId: Int) {
        if (index !in iconFlows.indices) return

        // SharedPref’ga saqlash
        when (index) {
            0 -> sharedPref.saveValue(KEY_BOMDOD, resId)
            1 -> sharedPref.saveValue(KEY_QUYOSH, resId)
            2 -> sharedPref.saveValue(KEY_PESHIN, resId)
            3 -> sharedPref.saveValue(KEY_ASR, resId)
            4 -> sharedPref.saveValue(KEY_SHOM, resId)
            5 -> sharedPref.saveValue(KEY_XUFTON, resId)
        }

        // StateFlow’ni yangilash
        iconFlows[index].value = resId
        refreshState()
    }

    /* -------------------- Yordamchi -------------------- */

    private fun refreshState() {
        if (currentNames.isEmpty()) return
        val list = iconFlows.mapIndexed { i, flow ->
            Notification(currentNames[i], flow.value)
        }
        _state.value = NotificationState.Success(list)
    }

    /* -------------------- Keys -------------------- */

    companion object {
        private const val KEY_BOMDOD = "bomdod"
        private const val KEY_QUYOSH = "quyoshChiqishi"
        private const val KEY_PESHIN = "peshin"
        private const val KEY_ASR = "asr"
        private const val KEY_SHOM = "shom"
        private const val KEY_XUFTON = "xufton"
    }
}
