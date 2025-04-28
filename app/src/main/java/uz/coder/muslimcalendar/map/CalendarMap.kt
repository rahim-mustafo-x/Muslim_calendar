package uz.coder.muslimcalendar.map

import uz.coder.muslimcalendar.models.db.MuslimCalendarDbModel
import uz.coder.muslimcalendar.models.db.SuraDbModel
import uz.coder.muslimcalendar.models.internet.PrayerTime
import uz.coder.muslimcalendar.models.internet.quran.QuranDTO
import uz.coder.muslimcalendar.models.internet.quran.SuraDTO
import uz.coder.muslimcalendar.models.internet.quran.SurahListDTO
import uz.coder.muslimcalendar.models.model.MuslimCalendar
import uz.coder.muslimcalendar.models.model.quran.Sura
import uz.coder.muslimcalendar.models.model.quran.SurahList

class CalendarMap {

    fun toMuslimCalendarDbModel(times: List<PrayerTime>) = times.map {  MuslimCalendarDbModel(
        it.day, it.hijriDate.day, it.hijriDate.month, it.month, it.region, it.weekday, it.times.asr, it.times.hufton, it.times.peshin, it.times.quyosh, it.times.shomIftor, it.times.tongSaharlik
    ) }

    fun toMuslimCalendar(model: MuslimCalendarDbModel) = MuslimCalendar(
        model.day,
        model.hijriDay,
        model.hijriMonth,
        model.month,
        model.region,
        model.weekday,
        model.asr,
        model.hufton,
        model.peshin,
        model.quyosh,
        model.shomIftor,
        model.tongSaharlik
    )

    fun toMuslimCalendarList(models: List<MuslimCalendarDbModel>) = models.map { toMuslimCalendar(it) }

    fun toSurahList(dTOS: List<SurahListDTO>?) = dTOS?.map {
        toSurah(it)
    }?:emptyList()

    private fun toSurah(dto: SurahListDTO) =
        SurahList(
            dto.arabicText?:"",
            dto.aya?:"",
            dto.footnotes?:"",
            dto.id?:"",
            dto.sura?:"",
            dto.translation?:""
        )

    fun toSuraDbModel(dTOS: List<SuraDTO>?) = dTOS?.map {
        SuraDbModel(
            it.number?:0,
            it.englishName?:"",
            it.englishNameTranslation?:"",
            it.name?:"",
            it.revelationType?:""
        )
    }?:emptyList()

    fun toSuraList(models: List<SuraDbModel>) = models.map {
        toSura(it)
    }

    fun toSura(model:SuraDbModel) = Sura(
        number = model.number,
        englishName = model.englishName,
        englishNameTranslation = model.englishNameTranslation,
        name = model.name,
        revelationType = model.revelationType
    )

}