package uz.coder.muslimcalendar.data.map

import uz.coder.muslimcalendar.data.db.model.MuslimCalendarDbModel
import uz.coder.muslimcalendar.data.db.model.SuraDbModel
import uz.coder.muslimcalendar.data.db.model.SurahAyahDbModel
import uz.coder.muslimcalendar.data.network.modelDTO.PrayerData
import uz.coder.muslimcalendar.data.network.modelDTO.quran.SuraDTO
import uz.coder.muslimcalendar.data.network.modelDTO.quran.SurahListDTO
import uz.coder.muslimcalendar.domain.model.MuslimCalendar
import uz.coder.muslimcalendar.models.model.SuraAyah
import uz.coder.muslimcalendar.domain.model.quran.Sura
import uz.coder.muslimcalendar.models.model.quran.SurahList
import uz.coder.muslimcalendar.todo.cyrillicToLatin
import uz.coder.muslimcalendar.todo.toNormalTranslate
import uz.coder.muslimcalendar.todo.toWeakDays
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CalendarMap @Inject constructor() {

    fun toMuslimCalendarDbModel(times: List<PrayerData?>?): List<MuslimCalendarDbModel> {
        return times?.map { prayerData ->
            val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            val date = LocalDate.parse(prayerData?.date?.gregorian?.date, formatter)

            val day = date.dayOfMonth
            val month = date.monthValue

            MuslimCalendarDbModel(
                day = day,
                hijriDay = prayerData?.date?.hijri?.day?.toIntOrNull() ?: 0,
                hijriMonth = prayerData?.date?.hijri?.month?.en?.toNormalTranslate() ?: "",
                month = month,
                weekday = prayerData?.date?.gregorian?.weekday?.en?.toWeakDays() ?: "",
                asr = prayerData?.timings?.asr?.substringBefore(" ")?.trim() ?: "",
                hufton = prayerData?.timings?.isha?.substringBefore(" ")?.trim() ?: "",
                peshin = prayerData?.timings?.dhuhr?.substringBefore(" ")?.trim() ?: "",
                sunrise = prayerData?.timings?.sunrise?.substringBefore(" ")?.trim() ?: "",
                shomIftor = prayerData?.timings?.maghrib?.substringBefore(" ")?.trim() ?: "",
                tongSaharlik = prayerData?.timings?.fajr?.substringBefore(" ")?.trim() ?: ""
            )
        }?:emptyList()
    }


    fun toMuslimCalendar(model: MuslimCalendarDbModel?) = MuslimCalendar(
        model?.day?:0,
        model?.hijriDay?:0,
        model?.hijriMonth?:"",
        model?.month?:0,
        model?.weekday?:"",
        model?.asr?:"",
        model?.hufton?:"",
        model?.peshin?:"",
        model?.shomIftor?:"",
        model?.tongSaharlik?:"",
        model?.sunrise?:""
    )

    fun toMuslimCalendarList(models: List<MuslimCalendarDbModel>) = models.map { toMuslimCalendar(it) }

    fun toSurahList(dTOS: List<SurahListDTO?>?) = dTOS?.map {
        toSurah(it)
    }?:emptyList()

    private fun toSurah(dto: SurahListDTO?) =
        SurahList(
            arabicText = dto?.arabicText?:"",
            aya = dto?.aya?:"",
            footnotes = dto?.footnotes?:"",
            id = dto?.id?:"",
            sura = dto?.sura?:"",
            translation = dto?.translation?.cyrillicToLatin()?:""
        )

    fun toSuraDbModel(dTOS: List<SuraDTO?>?) = dTOS?.map {
        SuraDbModel(
            it?.number?:0,
            it?.englishName?:"",
            it?.englishNameTranslation?:"",
            it?.name?:"",
            when(it?.revelationType?:""){
                "Meccan"->"Makka"
                "Medinan"->"Madina"
                else->""
            }
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