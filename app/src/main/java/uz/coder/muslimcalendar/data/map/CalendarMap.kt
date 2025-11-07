package uz.coder.muslimcalendar.data.map

import uz.coder.muslimcalendar.data.db.model.MuslimCalendarDbModel
import uz.coder.muslimcalendar.data.db.model.SuraDbModel
import uz.coder.muslimcalendar.data.db.model.SurahAyahDbModel
import uz.coder.muslimcalendar.data.network.modelDTO.PrayerData
import uz.coder.muslimcalendar.data.network.modelDTO.quran.SuraDTO
import uz.coder.muslimcalendar.data.network.modelDTO.quran.SurahListDTO
import uz.coder.muslimcalendar.domain.model.MuslimCalendar
import uz.coder.muslimcalendar.domain.model.quran.Sura
import uz.coder.muslimcalendar.models.model.SuraAyah
import uz.coder.muslimcalendar.domain.model.quran.SurahList
import uz.coder.muslimcalendar.todo.cyrillicToLatin
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
                month = month,
                weekday = prayerData?.date?.gregorian?.weekday?.en?.toWeakDays() ?: "",
                asr = prayerData?.timings?.asr?.replace(" (+05)", "")?:"",
                hufton = prayerData?.timings?.isha?.replace(" (+05)", "")?:"",
                peshin = prayerData?.timings?.dhuhr?.replace(" (+05)", "")?:"",
                sunrise = prayerData?.timings?.sunrise?.replace(" (+05)", "")?:"",
                shomIftor = prayerData?.timings?.maghrib?.replace(" (+05)", "")?:"",
                tongSaharlik = prayerData?.timings?.fajr?.replace(" (+05)", "")?:""
            )
        }?:emptyList()
    }


    fun toMuslimCalendar(model: MuslimCalendarDbModel?) = MuslimCalendar(
        model?.day?:0,
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
            footnotes = dto?.footnotes?.cyrillicToLatin()?:"",
            id = dto?.id?:"",
            sura = dto?.sura?:"",
            translation = dto?.translation?.cyrillicToLatin()?:""
        )

    fun toSuraDbModel(data:SuraDTO?) =
        SuraDbModel(
            number = data?.number?:0,
            name = data?.name?:"",
            englishName = data?.englishName?:"",
            englishNameTranslation = data?.englishNameTranslation?:"",
            numberOfAyahs = data?.numberOfAyahs?:0,
            revelationType = when(data?.revelationType?:""){
                "Meccan"->"Makka"
                "Medinan"->"Madina"
                else->""
            }
        )

    fun toSuraList(models: List<SuraDbModel>) = models.map {
        toSura(it)
    }

    fun toSura(model:SuraDbModel) = Sura(
        number = model.number,
        englishName = model.englishName,
        englishNameTranslation = model.englishNameTranslation,
        name = model.name,
        revelationType = model.revelationType,
        numberOfAyahs = model.numberOfAyahs
    )

    private fun toSuraAyah(model: SurahAyahDbModel) = SuraAyah(arabicText = model.arabicText, aya =  model.aya, footnotes =  model.footnotes, sura =  model.sura, translation =  model.translation.cyrillicToLatin(), id =  model.id)
    fun toSuraAyahList(list: List<SurahAyahDbModel>) = list.map { toSuraAyah(it) }
    fun toSuraAyahDbModels(model: List<SurahList>) = model.map { SurahAyahDbModel(arabicText = it.arabicText, aya =  it.aya, footnotes =  it.footnotes, sura =  it.sura, translation =  it.translation, id = it.id) }
}