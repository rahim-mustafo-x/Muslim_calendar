package uz.coder.muslimcalendar.domain.usecase

import uz.coder.muslimcalendar.domain.repository.CalendarRepository
import uz.coder.muslimcalendar.models.model.quran.SurahList

class DownloadSurahUseCase(private val repository: CalendarRepository) {
    operator fun invoke(surah: List<SurahList>, url:String) = repository.downloadSurah(surah, url)
}