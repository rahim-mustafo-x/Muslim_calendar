package uz.coder.muslimcalendar.map

import uz.coder.muslimcalendar.models.db.MuslimCalendarDbModel
import uz.coder.muslimcalendar.models.internet.PrayerTime
import uz.coder.muslimcalendar.models.internet.quran.QuranDTO
import uz.coder.muslimcalendar.models.internet.quran.SurahListDTO
import uz.coder.muslimcalendar.models.model.MuslimCalendar
import uz.coder.muslimcalendar.models.model.quran.Ayah
import uz.coder.muslimcalendar.models.model.quran.Sura
import uz.coder.muslimcalendar.models.model.quran.SurahList

class CalendarMap {
    fun toSuraList(quranDTO: QuranDTO?) = quranDTO?.surahs?.map {
        Sura(
            number =  it.number?:0,
            name =  it.name?:"",
            englishName =  it.englishName?:"",
            englishNameTranslation =  it.englishNameTranslation?:"",
            revelationType =  it.revelationType?:"",
            ayahs = it.ayahDTOS?.map {
                Ayah(
                    number = it.number?:0,
                    text = it.text?:"",
                    numberInSurah = it.numberInSurah?:0,
                    juz = it.juz?:0,
                    manzil = it.manzil?:0,
                    page = it.page?:0,
                    hizbQuarter = it.hizbQuarter?:0,
                    ruku = it.ruku?:0
                )
            }?:emptyList())
    }?:emptyList()

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

}