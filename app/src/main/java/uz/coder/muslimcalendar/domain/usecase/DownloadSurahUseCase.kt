package uz.coder.muslimcalendar.domain.usecase

import uz.coder.muslimcalendar.domain.repository.CalendarRepository
import uz.coder.muslimcalendar.domain.model.quran.SurahList

class DownloadSurahUseCase (private val repository: CalendarRepository) {
    suspend operator fun invoke(surah: List<SurahList>, url:String) = repository.downloadSurah(surah, url)
}