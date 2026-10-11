/**
 * Assumptions:
 * 1. Glance Theme automatically inherits Material 3 colors matching system light/dark mode.
 * 2. Per-prayer minute adjustments are loaded from SharedPref using keys: adj_bomdod, adj_quyosh, adj_peshin, adj_asr, adj_shom, adj_xufton.
 * 3. Quyosh (sunrise) is treated as a time boundary, not an active prayer window; post-Xufton periods until next day's Bomdod count as Xufton/Night with correct countdown across midnight.
 * 4. If calendar data is not downloaded or empty, widgets display "--:--" with a prompt "Taqvimni yuklab oling".
 */
package uz.coder.muslimcalendar.presentation.widget

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.*
import androidx.glance.action.clickable
import androidx.glance.appwidget.*
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.layout.*
import androidx.glance.text.*
import androidx.glance.unit.ColorProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import uz.coder.muslimcalendar.MainActivity
import uz.coder.muslimcalendar.R
import uz.coder.muslimcalendar.SharedPref
import uz.coder.muslimcalendar.data.db.AppDatabase
import uz.coder.muslimcalendar.data.map.CalendarMap
import uz.coder.muslimcalendar.todo.hijriMonthTranslations
import java.time.LocalDate
import java.time.LocalTime
import java.time.chrono.HijrahDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoField

class IslamicCalendarWidget : GlanceAppWidget() {

    companion object {
        private val SMALL_SQUARE = DpSize(100.dp, 100.dp)
        private val MEDIUM_RECT = DpSize(200.dp, 100.dp)
        private val LARGE_RECT = DpSize(300.dp, 200.dp)
    }

    override val sizeMode = SizeMode.Responsive(
        setOf(SMALL_SQUARE, MEDIUM_RECT, LARGE_RECT)
    )

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val state = withContext(Dispatchers.IO) {
            val today = LocalDate.now()
            val now = LocalTime.now()
            val db = AppDatabase.instance(context)
            val sharedPref = SharedPref(context)
            val map = CalendarMap()

            val locationName = sharedPref.getString("saved_location_name", "Toshkent")

            val todayModel = db.calendarDao().presentDay(today.dayOfMonth, today.monthValue, today.year).firstOrNull()
            val todayCalendar = map.toMuslimCalendar(todayModel)

            val tomorrow = today.plusDays(1)
            val tomorrowModel = db.calendarDao().presentDay(tomorrow.dayOfMonth, tomorrow.monthValue, tomorrow.year).firstOrNull()
            val tomorrowCalendar = map.toMuslimCalendar(tomorrowModel)

            val todayTimes = listOf(
                todayCalendar.tongSaharlik,
                todayCalendar.sunRise,
                todayCalendar.peshin,
                todayCalendar.asr,
                todayCalendar.shomIftor,
                todayCalendar.hufton
            )

            val tomorrowTimes = listOf(
                tomorrowCalendar.tongSaharlik,
                tomorrowCalendar.sunRise,
                tomorrowCalendar.peshin,
                tomorrowCalendar.asr,
                tomorrowCalendar.shomIftor,
                tomorrowCalendar.hufton
            )

            val adjustments = listOf(
                sharedPref.getInt("adj_bomdod", 0),
                sharedPref.getInt("adj_quyosh", 0),
                sharedPref.getInt("adj_peshin", 0),
                sharedPref.getInt("adj_asr", 0),
                sharedPref.getInt("adj_shom", 0),
                sharedPref.getInt("adj_xufton", 0)
            )

            calculateWidgetState(
                today = today,
                currentTime = now,
                todayPrayerTimes = todayTimes,
                tomorrowPrayerTimes = tomorrowTimes,
                adjustments = adjustments,
                locationName = locationName
            )
        }

        provideContent {
            GlanceTheme {
                val size = LocalSize.current
                val action = actionStartActivity(Intent(context, MainActivity::class.java))

                Box(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .background(GlanceTheme.colors.surface)
                        .cornerRadius(24.dp)
                        .clickable(action),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        size.width <= SMALL_SQUARE.width + 20.dp -> {
                            SmallWidget(state)
                        }
                        size.width <= MEDIUM_RECT.width + 50.dp -> {
                            MediumWidget(state)
                        }
                        else -> {
                            LargeWidget(state)
                        }
                    }
                }
            }
        }
    }
}

data class WidgetPrayerState(
    val hijriDay: Int,
    val hijriMonth: String,
    val hijriYear: Int,
    val gregorianDateStr: String,
    val weekdayStr: String,
    val dayProgress: Float,
    val currentPrayerName: String,
    val currentPrayerIndex: Int,
    val nextPrayerName: String,
    val nextPrayerTime: String,
    val remainingTime: String,
    val prayerNames: List<String>,
    val prayerTimeValues: List<String>,
    val isAvailable: Boolean,
    val locationName: String
)

fun calculateWidgetState(
    today: LocalDate,
    currentTime: LocalTime,
    todayPrayerTimes: List<String>?,
    tomorrowPrayerTimes: List<String>?,
    adjustments: List<Int>,
    locationName: String
): WidgetPrayerState {
    val hijriDate = HijrahDate.from(today)
    val hijriDay = hijriDate.get(ChronoField.DAY_OF_MONTH)
    val hijriYear = hijriDate.get(ChronoField.YEAR)
    val hijriMonth = getHijriMonthNameInternal(hijriDate)

    val formatter = DateTimeFormatter.ofPattern("HH:mm")

    val monthsUz = listOf("Yanvar", "Fevral", "Mart", "Aprel", "May", "Iyun", "Iyul", "Avgust", "Sentabr", "Oktabr", "Noyabr", "Dekabr")
    val weekdaysUz = listOf("Dushanba", "Seshanba", "Chorshanba", "Payshanba", "Juma", "Shanba", "Yakshanba")

    val gregorianDateStr = "${today.dayOfMonth} ${monthsUz.getOrElse(today.monthValue - 1) { "" }} ${today.year}"
    val weekdayStr = weekdaysUz.getOrElse(today.dayOfWeek.value - 1) { "" }

    val totalSecondsInDay = 24 * 60 * 60
    val secondsPassed = currentTime.toSecondOfDay()
    val dayProgress = secondsPassed.toFloat() / totalSecondsInDay

    val prayerNames = listOf("Bomdod", "Quyosh", "Peshin", "Asr", "Shom", "Xufton")

    if (todayPrayerTimes == null || todayPrayerTimes.size < 6 || todayPrayerTimes.all { it.isEmpty() || it == "--:--" }) {
        return WidgetPrayerState(
            hijriDay = hijriDay,
            hijriMonth = hijriMonth,
            hijriYear = hijriYear,
            gregorianDateStr = gregorianDateStr,
            weekdayStr = weekdayStr,
            dayProgress = dayProgress,
            currentPrayerName = "Tun",
            currentPrayerIndex = -1,
            nextPrayerName = "Bomdod",
            nextPrayerTime = "--:--",
            remainingTime = "",
            prayerNames = prayerNames,
            prayerTimeValues = listOf("--:--", "--:--", "--:--", "--:--", "--:--", "--:--"),
            isAvailable = false,
            locationName = locationName
        )
    }

    val adjustedTimes = mutableListOf<LocalTime?>()
    val adjustedTimeStrs = mutableListOf<String>()
    for (i in 0..5) {
        val tStr = todayPrayerTimes.getOrElse(i) { "" }
        val adj = adjustments.getOrElse(i) { 0 }
        if (tStr.isEmpty() || tStr == "--:--") {
            adjustedTimes.add(null)
            adjustedTimeStrs.add("--:--")
        } else {
            try {
                val parsed = LocalTime.parse(tStr.trim().take(5), formatter)
                val adjusted = parsed.plusMinutes(adj.toLong())
                adjustedTimes.add(adjusted)
                adjustedTimeStrs.add(adjusted.format(formatter))
            } catch (e: Exception) {
                adjustedTimes.add(null)
                adjustedTimeStrs.add("--:--")
            }
        }
    }

    val tomorrowBomdod: LocalTime? = runCatching {
        val tStr = tomorrowPrayerTimes?.getOrElse(0) { "" } ?: ""
        val adj = adjustments.getOrElse(0) { 0 }
        if (tStr.isNotEmpty() && tStr != "--:--") {
            LocalTime.parse(tStr.trim().take(5), formatter).plusMinutes(adj.toLong())
        } else null
    }.getOrNull()

    val bomdod = adjustedTimes.getOrNull(0)
    val quyosh = adjustedTimes.getOrNull(1)
    val peshin = adjustedTimes.getOrNull(2)
    val asr = adjustedTimes.getOrNull(3)
    val shom = adjustedTimes.getOrNull(4)
    val xufton = adjustedTimes.getOrNull(5)

    var currentPrayerName = "Tun"
    var currentPrayerIndex = -1
    var nextPrayerName = "Bomdod"
    var nextPrayerTime = "--:--"
    var nextPrayerLocalTime: LocalTime? = null
    var isNextPrayerTomorrow = false

    when {
        bomdod != null && currentTime.isBefore(bomdod) -> {
            currentPrayerName = "Xufton"
            currentPrayerIndex = 5
            nextPrayerName = "Bomdod"
            nextPrayerTime = adjustedTimeStrs[0]
            nextPrayerLocalTime = bomdod
        }
        bomdod != null && (quyosh == null || currentTime.isBefore(quyosh)) -> {
            currentPrayerName = "Bomdod"
            currentPrayerIndex = 0
            nextPrayerName = "Quyosh"
            nextPrayerTime = adjustedTimeStrs[1]
            nextPrayerLocalTime = quyosh
        }
        quyosh != null && (peshin == null || currentTime.isBefore(peshin)) -> {
            currentPrayerName = "Bomdod"
            currentPrayerIndex = 0
            nextPrayerName = "Peshin"
            nextPrayerTime = adjustedTimeStrs[2]
            nextPrayerLocalTime = peshin
        }
        peshin != null && (asr == null || currentTime.isBefore(asr)) -> {
            currentPrayerName = "Peshin"
            currentPrayerIndex = 2
            nextPrayerName = "Asr"
            nextPrayerTime = adjustedTimeStrs[3]
            nextPrayerLocalTime = asr
        }
        asr != null && (shom == null || currentTime.isBefore(shom)) -> {
            currentPrayerName = "Asr"
            currentPrayerIndex = 3
            nextPrayerName = "Shom"
            nextPrayerTime = adjustedTimeStrs[4]
            nextPrayerLocalTime = shom
        }
        shom != null && (xufton == null || currentTime.isBefore(xufton)) -> {
            currentPrayerName = "Shom"
            currentPrayerIndex = 4
            nextPrayerName = "Xufton"
            nextPrayerTime = adjustedTimeStrs[5]
            nextPrayerLocalTime = xufton
        }
        else -> {
            currentPrayerName = "Xufton"
            currentPrayerIndex = 5
            if (tomorrowBomdod != null) {
                nextPrayerName = "Bomdod (Ertaga)"
                nextPrayerTime = tomorrowPrayerTimes?.getOrElse(0) { "" } ?: "--:--"
                nextPrayerLocalTime = tomorrowBomdod
                isNextPrayerTomorrow = true
            } else {
                nextPrayerName = "Bomdod"
                nextPrayerTime = "--:--"
            }
        }
    }

    val remainingTime = if (nextPrayerLocalTime != null) {
        calculateRemainingTimePure(currentTime, nextPrayerLocalTime, isNextPrayerTomorrow)
    } else ""

    return WidgetPrayerState(
        hijriDay = hijriDay,
        hijriMonth = hijriMonth,
        hijriYear = hijriYear,
        gregorianDateStr = gregorianDateStr,
        weekdayStr = weekdayStr,
        dayProgress = dayProgress,
        currentPrayerName = currentPrayerName,
        currentPrayerIndex = currentPrayerIndex,
        nextPrayerName = nextPrayerName,
        nextPrayerTime = nextPrayerTime,
        remainingTime = remainingTime,
        prayerNames = prayerNames,
        prayerTimeValues = adjustedTimeStrs,
        isAvailable = true,
        locationName = locationName
    )
}

private fun calculateRemainingTimePure(now: LocalTime, target: LocalTime, isTomorrow: Boolean): String {
    return try {
        val nowSec = now.toSecondOfDay()
        val targetSec = target.toSecondOfDay()
        val diffSec = if (isTomorrow) {
            (24 * 60 * 60 - nowSec) + targetSec
        } else {
            val d = targetSec - nowSec
            if (d < 0) d + 24 * 60 * 60 else d
        }
        val totalMinutes = diffSec / 60
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60
        buildString {
            if (hours > 0) append("$hours soat ")
            append("$minutes daqiqa")
        }
    } catch (e: Exception) {
        ""
    }
}

private fun getHijriMonthNameInternal(date: HijrahDate): String {
    val months = listOf(
        "Muharram", "Safar", "Rabi al-awwal", "Rabi al-thani",
        "Jumada al-awwal", "Jumada al-thani", "Rajab", "Shaʿbān",
        "Ramadan", "Shawwāl", "Dhū al-Qaʿdah", "Dhū al-Ḥijjah"
    )
    val monthIdx = date.get(ChronoField.MONTH_OF_YEAR) - 1
    val key = months.getOrElse(monthIdx) { "" }
    return hijriMonthTranslations[key] ?: key
}

@Composable
private fun SmallWidget(state: WidgetPrayerState) {
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = GlanceModifier
                .size(56.dp)
                .background(GlanceTheme.colors.primaryContainer)
                .cornerRadius(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = state.hijriDay.toString(),
                style = TextStyle(
                    color = GlanceTheme.colors.onPrimaryContainer,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }
        Spacer(modifier = GlanceModifier.height(4.dp))
        Text(
            text = state.hijriMonth,
            style = TextStyle(
                color = GlanceTheme.colors.onSurface,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        )
        Spacer(modifier = GlanceModifier.height(6.dp))
        if (state.isAvailable) {
            Text(
                text = "${state.nextPrayerName} • ${state.nextPrayerTime}",
                style = TextStyle(
                    color = GlanceTheme.colors.primary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        } else {
            Text(
                text = "Taqvimni yuklab oling",
                style = TextStyle(
                    color = GlanceTheme.colors.error,
                    fontSize = 9.sp
                )
            )
        }
    }
}

@Composable
private fun MediumWidget(state: WidgetPrayerState) {
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(GlanceTheme.colors.primaryContainer)
            .cornerRadius(24.dp)
            .padding(16.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        Image(
            provider = ImageProvider(R.drawable.mosque),
            contentDescription = "Mosque background image",
            modifier = GlanceModifier.size(72.dp)
        )

        Column(
            modifier = GlanceModifier.fillMaxSize(),
            verticalAlignment = Alignment.Vertical.CenterVertically
        ) {
            Text(
                text = "Keyingi namoz",
                style = TextStyle(
                    color = GlanceTheme.colors.primary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = GlanceModifier.height(2.dp))
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.Vertical.CenterVertically
            ) {
                Text(
                    text = state.nextPrayerName,
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurface,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = GlanceModifier.defaultWeight())
                Text(
                    text = state.nextPrayerTime,
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurface,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            if (state.isAvailable) {
                Spacer(modifier = GlanceModifier.height(2.dp))
                Text(
                    text = "${state.remainingTime} qoldi",
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurface,
                        fontSize = 12.sp
                    )
                )
            } else {
                Spacer(modifier = GlanceModifier.height(2.dp))
                Text(
                    text = "Taqvim yuklanmagan",
                    style = TextStyle(
                        color = GlanceTheme.colors.error,
                        fontSize = 11.sp
                    )
                )
            }
            Spacer(modifier = GlanceModifier.height(8.dp))
            LinearProgressIndicator(
                progress = state.dayProgress,
                modifier = GlanceModifier.fillMaxWidth().height(4.dp),
                color = GlanceTheme.colors.primary,
                backgroundColor = GlanceTheme.colors.surfaceVariant
            )
        }
    }
}

@Composable
private fun LargeWidget(state: WidgetPrayerState) {
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(GlanceTheme.colors.surface)
            .cornerRadius(24.dp)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalAlignment = Alignment.Vertical.Top
    ) {
        // Header
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.Vertical.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.Vertical.CenterVertically) {
                Image(
                    provider = ImageProvider(R.drawable.mosque),
                    contentDescription = "App logo icon",
                    modifier = GlanceModifier.size(20.dp)
                )
                Spacer(modifier = GlanceModifier.width(6.dp))
                Text(
                    text = "Muslim Taqvim",
                    style = TextStyle(
                        color = GlanceTheme.colors.primary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            Spacer(modifier = GlanceModifier.defaultWeight())
            Row(
                modifier = GlanceModifier
                    .background(GlanceTheme.colors.surfaceVariant)
                    .cornerRadius(12.dp)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.Vertical.CenterVertically
            ) {
                Text(
                    text = state.locationName.ifBlank { "Toshkent" },
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurfaceVariant,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }

        Spacer(modifier = GlanceModifier.height(10.dp))

        // Next prayer card block
        Box(
            modifier = GlanceModifier
                .fillMaxWidth()
                .background(GlanceTheme.colors.primaryContainer)
                .cornerRadius(16.dp)
                .padding(12.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            Image(
                provider = ImageProvider(R.drawable.mosque),
                contentDescription = "Mosque watermark",
                modifier = GlanceModifier.size(48.dp)
            )
            Column(modifier = GlanceModifier.fillMaxWidth()) {
                Text(
                    text = "Keyingi namoz • ${state.nextPrayerName}",
                    style = TextStyle(
                        color = GlanceTheme.colors.primary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Vertical.CenterVertically
                ) {
                    Text(
                        text = state.nextPrayerTime,
                        style = TextStyle(
                            color = GlanceTheme.colors.onSurface,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = GlanceModifier.defaultWeight())
                    if (state.isAvailable) {
                        Text(
                            text = "${state.remainingTime} qoldi",
                            style = TextStyle(
                                color = GlanceTheme.colors.onSurface,
                                fontSize = 11.sp
                            )
                        )
                    } else {
                        Text(
                            text = "Taqvimni yuklab oling",
                            style = TextStyle(
                                color = GlanceTheme.colors.error,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = GlanceModifier.height(12.dp))

        // Prayer Time Row (all 6 prayers)
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.Vertical.CenterVertically
        ) {
            state.prayerNames.forEachIndexed { index, name ->
                val isCurrent = index == state.currentPrayerIndex
                if (index > 0) {
                    Spacer(modifier = GlanceModifier.defaultWeight())
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = name,
                        style = TextStyle(
                            color = if (isCurrent) GlanceTheme.colors.primary else GlanceTheme.colors.onSurface,
                            fontSize = 10.sp,
                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                        )
                    )
                    Spacer(modifier = GlanceModifier.height(2.dp))
                    Text(
                        text = state.prayerTimeValues.getOrElse(index) { "--:--" },
                        style = TextStyle(
                            color = if (isCurrent) GlanceTheme.colors.primary else GlanceTheme.colors.onSurface,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = GlanceModifier.height(2.dp))
                    val dotModifier = if (isCurrent) {
                        GlanceModifier
                            .size(4.dp)
                            .background(GlanceTheme.colors.primary)
                            .cornerRadius(2.dp)
                    } else {
                        GlanceModifier
                            .size(4.dp)
                            .cornerRadius(2.dp)
                    }
                    Box(modifier = dotModifier) {}
                }
            }
        }

        Spacer(modifier = GlanceModifier.defaultWeight())

        // Date footer
        Text(
            text = "${state.gregorianDateStr} • ${state.hijriDay} ${state.hijriMonth} ${state.hijriYear}",
            style = TextStyle(
                color = GlanceTheme.colors.onSurface,
                fontSize = 10.sp
            )
        )
    }
}
