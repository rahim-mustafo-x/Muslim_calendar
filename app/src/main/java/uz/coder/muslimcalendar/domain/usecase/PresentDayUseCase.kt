package uz.coder.muslimcalendar.domain.usecase

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import uz.coder.muslimcalendar.domain.model.MuslimCalendar
import uz.coder.muslimcalendar.domain.repository.CalendarRepository
import javax.inject.Inject

class PresentDayUseCase @Inject constructor(
    private val repository: CalendarRepository
) {

    private var cachedFlow: Flow<MuslimCalendar>? = null

    operator fun invoke(): Flow<MuslimCalendar> {
        // Faqat bir marta Flow yaratish va cache qilish
        if (cachedFlow == null) {
            cachedFlow = repository.presentDay()
                .distinctUntilChanged() // Faqat o'zgarganda emit qilish
                .catch { e ->
                    // Xatolikda bo'sh ma'lumot qaytarish
                    Log.e("PresentDayUseCase", "Error getting present day", e)
                    emit(MuslimCalendar())
                }
                .flowOn(Dispatchers.IO)
        }
        return cachedFlow!!
    }

    fun clearCache() {
        cachedFlow = null
    }
}