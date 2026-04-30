package uz.coder.muslimcalendar.presentation.widget

import android.annotation.SuppressLint
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
import kotlinx.coroutines.flow.firstOrNull
import uz.coder.muslimcalendar.MainActivity
import uz.coder.muslimcalendar.R
import uz.coder.muslimcalendar.SharedPref
import uz.coder.muslimcalendar.data.db.AppDatabase
import uz.coder.muslimcalendar.data.map.CalendarMap
import uz.coder.muslimcalendar.domain.model.MuslimCalendar
import uz.coder.muslimcalendar.todo.hijriMonthTranslations
import java.time.LocalDate
import java.time.LocalTime
import java.time.chrono.HijrahDate
import java.time.format.DateTimeFormatter
import java.util.*

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
        // Load data once per update
        val today = LocalDate.now()
        val now = LocalTime.now()
        val hijriDate = HijrahDate.from(today)
        
        val hijriDay = hijriDate.get(java.time.temporal.ChronoField.DAY_OF_MONTH)
        val hijriMonth = getHijriMonthName(hijriDate)
        val hijriYear = hijriDate.get(java.time.temporal.ChronoField.YEAR)
        
        val gregorianDateStr = today.format(DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH))
        val weekdayStr = today.format(DateTimeFormatter.ofPattern("EEEE", Locale.ENGLISH))
        
        // Calculate day progress for the progress bar
        val totalSecondsInDay = 24 * 60 * 60
        val secondsPassed = now.toSecondOfDay()
        val dayProgress = secondsPassed.toFloat() / totalSecondsInDay

        // Fetch prayer times
        val db = AppDatabase.instance(context)
        val sharedPref = SharedPref(context)
        val map = CalendarMap()
        
        val todayModel = db.calendarDao().presentDay(today.dayOfMonth, today.monthValue, today.year).firstOrNull()
        val prayerData = map.toMuslimCalendar(todayModel)
        
        val adjustments = listOf(
            sharedPref.getInt("adj_bomdod", 0),
            sharedPref.getInt("adj_quyosh", 0),
            sharedPref.getInt("adj_peshin", 0),
            sharedPref.getInt("adj_asr", 0),
            sharedPref.getInt("adj_shom", 0),
            sharedPref.getInt("adj_xufton", 0)
        )
        
        val prayerTimes = listOf(
            prayerData.tongSaharlik,
            prayerData.sunRise,
            prayerData.peshin,
            prayerData.asr,
            prayerData.shomIftor,
            prayerData.hufton
        ).mapIndexed { index, time ->
            if (time.isEmpty()) return@mapIndexed null
            try {
                val (h, m) = time.split(":").map { it.toInt() }
                LocalTime.of(h, m).plusMinutes(adjustments[index].toLong())
            } catch (e: Exception) {
                null
            }
        }

        val prayerNames = listOf("Bomdod", "Quyosh", "Peshin", "Asr", "Shom", "Xufton")
        
        var currentPrayerIndex = -1
        var nextPrayerIndex = 0
        
        for (i in prayerTimes.indices) {
            val time = prayerTimes[i] ?: continue
            if (now.isAfter(time)) {
                currentPrayerIndex = i
            } else {
                nextPrayerIndex = i
                break
            }
        }
        
        // If it's after Xufton, next is tomorrow's Bomdod
        val nextPrayerName: String
        val nextPrayerTime: String
        if (currentPrayerIndex == 5) {
            nextPrayerName = "Bomdod (Ertaga)"
            // For simplicity, we just show the name, or we could fetch tomorrow's data
            val tomorrow = today.plusDays(1)
            val tomorrowModel = db.calendarDao().presentDay(tomorrow.dayOfMonth, tomorrow.monthValue, tomorrow.year).firstOrNull()
            val tomorrowData = map.toMuslimCalendar(tomorrowModel)
            nextPrayerTime = if (tomorrowData.tongSaharlik.isNotEmpty()) {
                val (h, m) = tomorrowData.tongSaharlik.split(":").map { it.toInt() }
                LocalTime.of(h, m).plusMinutes(adjustments[0].toLong()).toString()
            } else "--:--"
        } else {
            nextPrayerName = prayerNames[nextPrayerIndex]
            nextPrayerTime = prayerTimes[nextPrayerIndex]?.toString() ?: "--:--"
        }
        
        val currentPrayerName = if (currentPrayerIndex != -1) prayerNames[currentPrayerIndex] else "Tun"
        
        val prayerTimeValues = listOf(
            prayerData.tongSaharlik,
            prayerData.sunRise,
            prayerData.peshin,
            prayerData.asr,
            prayerData.shomIftor,
            prayerData.hufton
        )

        provideContent {
            val size = LocalSize.current
            
            Box(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(Color(0xFF191C1C)) // DarkBackground
                    .cornerRadius(24.dp)
                    .clickable(actionStartActivity(Intent(context, MainActivity::class.java))),
                contentAlignment = Alignment.Center
            ) {
                when {
                    size.width <= SMALL_SQUARE.width -> {
                        SmallWidget(hijriDay)
                    }
                    size.width <= MEDIUM_RECT.width -> {
                        MediumWidget(hijriDay, hijriMonth, weekdayStr, dayProgress, currentPrayerName, nextPrayerName, nextPrayerTime)
                    }
                    else -> {
                        LargeWidget(hijriDay, hijriMonth, hijriYear, gregorianDateStr, weekdayStr, dayProgress, currentPrayerName, nextPrayerName, nextPrayerTime, prayerNames, prayerTimeValues, currentPrayerIndex)
                    }
                }
            }
        }
    }

    @SuppressLint("RestrictedApi")
    @Composable
    private fun SmallWidget(day: Int) {
        Column(
            modifier = GlanceModifier.fillMaxSize().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = GlanceModifier
                    .size(64.dp)
                    .background(Color(0xFF3F4947)) // DarkSurfaceVariant
                    .cornerRadius(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = day.toString(),
                    style = TextStyle(
                        color = ColorProvider(Color(0xFF54DBC8)), // DarkPrimary
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            Spacer(modifier = GlanceModifier.height(4.dp))
            Text(
                text = "HIJRI",
                style = TextStyle(
                    color = ColorProvider(Color(0xFFBEC9C6)), // DarkOnSurfaceVariant
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }

    @SuppressLint("RestrictedApi")
    @Composable
    private fun MediumWidget(day: Int, month: String, weekday: String, progress: Float, currentPrayer: String, nextPrayer: String, nextTime: String) {
        Row(
            modifier = GlanceModifier.fillMaxSize().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.Start
        ) {
            Column(
                modifier = GlanceModifier.defaultWeight(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$day $month",
                    style = TextStyle(
                        color = ColorProvider(Color(0xFFE0E3E2)), // DarkOnSurface
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "$currentPrayer • $nextPrayer: $nextTime",
                    style = TextStyle(
                        color = ColorProvider(Color(0xFF54DBC8)), // DarkPrimary
                        fontSize = 13.sp
                    )
                )
                Spacer(modifier = GlanceModifier.height(12.dp))
                LinearProgressIndicator(
                    progress = progress,
                    modifier = GlanceModifier.fillMaxWidth().height(4.dp),
                    color = ColorProvider(Color(0xFF54DBC8)), // DarkPrimary
                    backgroundColor = ColorProvider(Color(0xFF3F4947)) // DarkSurfaceVariant
                )
            }
            Spacer(modifier = GlanceModifier.width(12.dp))
            Image(
                provider = ImageProvider(R.drawable.safa_icon),
                contentDescription = "Safa Icon",
                modifier = GlanceModifier.size(48.dp)
            )
        }
    }

    @SuppressLint("RestrictedApi")
    @Composable
    private fun LargeWidget(
        day: Int,
        month: String,
        year: Int,
        gregorian: String,
        weekday: String,
        progress: Float,
        currentPrayer: String,
        nextPrayer: String,
        nextTime: String,
        prayerNames: List<String>,
        prayerTimes: List<String>,
        currentIndex: Int
    ) {
        Column(
            modifier = GlanceModifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.Top
        ) {
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    provider = ImageProvider(R.drawable.safa_icon),
                    contentDescription = "Safa Icon",
                    modifier = GlanceModifier.size(24.dp)
                )
                Spacer(modifier = GlanceModifier.width(8.dp))
                Text(
                    text = "Muslim Taqvim",
                    style = TextStyle(
                        color = ColorProvider(Color(0xFF54DBC8)), // DarkPrimary
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            
            Spacer(modifier = GlanceModifier.height(12.dp))
            
            Row(
                modifier = GlanceModifier.fillMaxWidth().defaultWeight(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left side: Progress and Time
                Column(
                    modifier = GlanceModifier.width(100.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")),
                        style = TextStyle(
                            color = ColorProvider(Color.White),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "$day $month",
                        style = TextStyle(
                            color = ColorProvider(Color(0xFFBEC9C6)),
                            fontSize = 12.sp
                        )
                    )
                    Spacer(modifier = GlanceModifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = progress,
                        modifier = GlanceModifier.fillMaxWidth().height(4.dp),
                        color = ColorProvider(Color(0xFF54DBC8)),
                        backgroundColor = ColorProvider(Color(0xFF3F4947))
                    )
                }
                
                Spacer(modifier = GlanceModifier.width(16.dp))
                
                // Right side: Prayer List
                Column(modifier = GlanceModifier.defaultWeight()) {
                    prayerNames.forEachIndexed { index, name ->
                        val isCurrent = index == currentIndex
                        Row(
                            modifier = GlanceModifier.fillMaxWidth().padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalAlignment = Alignment.Start
                        ) {
                            Box(
                                modifier = GlanceModifier
                                    .size(4.dp)
                                    .background(if (isCurrent) Color(0xFF54DBC8) else Color.Transparent)
                                    .cornerRadius(2.dp),
                                content = {}
                            )
                            Spacer(modifier = GlanceModifier.width(6.dp))
                            Text(
                                text = name,
                                modifier = GlanceModifier.defaultWeight(),
                                style = TextStyle(
                                    color = ColorProvider(if (isCurrent) Color(0xFF54DBC8) else Color(0xFFE0E3E2)),
                                    fontSize = 11.sp,
                                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                            Text(
                                text = prayerTimes[index],
                                style = TextStyle(
                                    color = ColorProvider(if (isCurrent) Color(0xFF54DBC8) else Color(0xFFBEC9C6)),
                                    fontSize = 11.sp,
                                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = GlanceModifier.height(8.dp))
            
            // Bottom branding
            Text(
                text = "Safa",
                style = TextStyle(
                    color = ColorProvider(Color(0x80BEC9C6)), // 50% alpha
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }

    private fun getHijriMonthName(date: HijrahDate): String {
        val months = listOf(
            "Muharram", "Safar", "Rabi al-awwal", "Rabi al-thani",
            "Jumada al-awwal", "Jumada al-thani", "Rajab", "Shaʿbān",
            "Ramadan", "Shawwāl", "Dhū al-Qaʿdah", "Dhū al-Ḥijjah"
        )
        val monthIdx = date.get(java.time.temporal.ChronoField.MONTH_OF_YEAR) - 1
        val key = months.getOrElse(monthIdx) { "" }
        return hijriMonthTranslations[key] ?: key
    }
}
