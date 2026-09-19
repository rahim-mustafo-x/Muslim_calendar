package uz.coder.muslimcalendar.data.map

import uz.coder.muslimcalendar.data.db.model.AudioPathDbModel
import uz.coder.muslimcalendar.data.db.model.MuslimCalendarDbModel
import uz.coder.muslimcalendar.data.db.model.SuraDbModel
import uz.coder.muslimcalendar.data.db.model.SurahAyahDbModel
import uz.coder.muslimcalendar.data.network.modelDTO.PrayerData
import uz.coder.muslimcalendar.data.network.modelDTO.quran.SuraDTO
import uz.coder.muslimcalendar.data.network.modelDTO.quran.SurahListDTO
import uz.coder.muslimcalendar.domain.model.AudioPath
import uz.coder.muslimcalendar.shared.domain.model.MuslimCalendar
import uz.coder.muslimcalendar.domain.model.quran.Sura
import uz.coder.muslimcalendar.domain.model.quran.Surah
import uz.coder.muslimcalendar.domain.model.SuraAyah
import uz.coder.muslimcalendar.domain.model.quran.SurahList
import uz.coder.muslimcalendar.todo.cyrillicToLatin
import uz.coder.muslimcalendar.todo.toWeakDays
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CalendarMap {

    fun toMuslimCalendarDbModel(times: List<PrayerData?>?): List<MuslimCalendarDbModel> {
        return times?.filterNotNull()?.map { prayerData ->
            val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            val date = LocalDate.parse(prayerData.date?.gregorian?.date, formatter)

            val day = date.dayOfMonth
            val month = date.monthValue
            MuslimCalendarDbModel(
                day = day,
                month = month,
                year = date.year,
                weekday = prayerData.date?.gregorian?.weekday?.en?.toWeakDays() ?: "",
                asr = prayerData.timings?.asr?.substringBefore(" ")?:"",
                hufton = prayerData.timings?.isha?.substringBefore(" ")?:"",
                peshin = prayerData.timings?.dhuhr?.substringBefore(" ")?:"",
                sunrise = prayerData.timings?.sunrise?.substringBefore(" ")?:"",
                shomIftor = prayerData.timings?.maghrib?.substringBefore(" ")?:"",
                tongSaharlik = prayerData.timings?.fajr?.substringBefore(" ")?:""
            )
        }?:emptyList()
    }


    fun toMuslimCalendar(model: MuslimCalendarDbModel?) = MuslimCalendar(
        day = model?.day?:0,
        month = model?.month?:0,
        year = model?.year?:0,
        weekday = model?.weekday?:"",
        asr = model?.asr?:"",
        hufton = model?.hufton?:"",
        peshin = model?.peshin?:"",
        shomIftor = model?.shomIftor?:"",
        tongSaharlik = model?.tongSaharlik?:"",
        sunRise = model?.sunrise?:""
    )

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

    fun toSura(model:SuraDbModel) = Sura(
        number = model.number,
        englishName = model.englishName,
        englishNameTranslation = model.englishNameTranslation,
        name = model.name,
        revelationType = model.revelationType,
        numberOfAyahs = model.numberOfAyahs
    )

    fun toSuraAyah(model: SurahAyahDbModel) = SuraAyah(arabicText = model.arabicText, aya =  model.aya, footnotes =  model.footnotes, sura =  model.sura, translation =  model.translation.cyrillicToLatin(), id =  model.id)
    fun toSuraAyahDbModels(model: List<SurahList>) = model.map { SurahAyahDbModel(arabicText = it.arabicText, aya =  it.aya, footnotes =  it.footnotes, sura =  it.sura, translation =  it.translation, id = it.id) }
    fun toSurah(
        entities: List<SurahAyahDbModel>
    ): Surah {
        return Surah(
            result = entities.map { entity ->
                SurahList(
                    id = entity.id,
                    sura = entity.sura,
                    aya = entity.aya,
                    arabicText = entity.arabicText,
                    translation = entity.translation,
                    footnotes = entity.footnotes
                )
            }
        )
    }
    fun toAudioPath(
        entity: AudioPathDbModel
    ): AudioPath {
        return AudioPath(
            sura = entity.sura,
            path = entity.audioPath
        )
    }
}