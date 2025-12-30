package uz.coder.muslimcalendar.domain.usecase

import android.util.Log
import uz.coder.muslimcalendar.domain.repository.CalendarRepository
import javax.inject.Inject

class RemoveUseCase @Inject constructor(
    private val repository: CalendarRepository
) {

    suspend operator fun invoke() {
        try {
            repository.remove()
        } catch (e: Exception) {
            Log.e("RemoveUseCase", "Error removing old data", e)
            // Xatolikda exception tashlamaslik, faqat log qilish
        }
    }
}