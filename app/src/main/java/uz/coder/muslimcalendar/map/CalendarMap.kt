package uz.coder.muslimcalendar.map

import uz.coder.muslimcalendar.db.model.MuslimCalendarDbModel
import uz.coder.muslimcalendar.db.model.SuraDbModel
import uz.coder.muslimcalendar.db.model.SurahAyahDbModel
import uz.coder.muslimcalendar.ktor.internet.PrayerData
import uz.coder.muslimcalendar.ktor.internet.quran.SuraDTO
import uz.coder.muslimcalendar.ktor.internet.quran.SurahListDTO
import uz.coder.muslimcalendar.models.model.MuslimCalendar
import uz.coder.muslimcalendar.models.model.SuraAyah
import uz.coder.muslimcalendar.models.model.quran.Sura
import uz.coder.muslimcalendar.models.model.quran.SurahList
import uz.coder.muslimcalendar.todo.cyrillicToLatin
import uz.coder.muslimcalendar.todo.toNormalTranslate
import uz.coder.muslimcalendar.todo.toWeakDays
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CalendarMap {

    fun toMuslimCalendarDbModel(times: List<PrayerData>): List<MuslimCalendarDbModel> {
        return times.map { prayerData ->
            val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            val date = LocalDate.parse(prayerData.date.gregorian.date, formatter)

            val day = date.dayOfMonth
            val month = date.monthValue
            MuslimCalendarDbModel(
                day = day,
                hijriDay = prayerData.date.hijri.day.toIntOrNull() ?: 0,
                hijriMonth = prayerData.date.hijri.month.en.toNormalTranslate(),
                month = month,
                weekday = prayerData.date.gregorian.weekday.en.toWeakDays(),
                asr = prayerData.timings.asr.substringBefore(" ").trim(),
                hufton = prayerData.timings.isha.substringBefore(" ").trim(),
                peshin = prayerData.timings.dhuhr.substringBefore(" ").trim(),
                sunset = prayerData.timings.sunset.substringBefore(" ").trim(),   // Quyosh botishi
                sunrise = prayerData.timings.sunrise.substringBefore(" ").trim(),   // Quyosh botishi
                shomIftor = prayerData.timings.maghrib.substringBefore(" ").trim(),
                tongSaharlik = prayerData.timings.fajr.substringBefore(" ").trim()
            )
        }
    }

    fun toMuslimCalendar(model: MuslimCalendarDbModel) = MuslimCalendar(
        model.day,
        model.hijriDay,
        model.hijriMonth,
        model.month,
        model.weekday,
        model.asr,
        model.hufton,
        model.peshin,
        model.shomIftor,
        model.tongSaharlik,
        model.sunset,
        model.sunrise
    )

    fun toMuslimCalendarList(models: List<MuslimCalendarDbModel>) = models.map { toMuslimCalendar(it) }

    fun toSurahList(dTOS: List<SurahListDTO>?) = dTOS?.map {
        toSurah(it)
    }?:emptyList()

    private fun toSurah(dto: SurahListDTO) =
        SurahList(
            arabicText = dto.arabicText?:"",
            aya = dto.aya?:"",
            footnotes = dto.footnotes?:"",
            id = dto.id?:"",
            sura = dto.sura?:"",
            translation = dto.translation?.cyrillicToLatin()?:""
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

    private fun toSuraAyah(model: SurahAyahDbModel) = SuraAyah(arabicText = model.arabicText, aya =  model.aya, footnotes =  model.footnotes, sura =  model.sura, translation =  model.translation.cyrillicToLatin(), id =  model.id)
    fun toSuraAyahList(list: List<SurahAyahDbModel>) = list.map { toSuraAyah(it) }
    fun toSuraAyahDbModels(model: List<SurahList>) = model.map { SurahAyahDbModel(arabicText = it.arabicText, aya =  it.aya, footnotes =  it.footnotes, sura =  it.sura, translation =  it.translation, id = it.id) }
}