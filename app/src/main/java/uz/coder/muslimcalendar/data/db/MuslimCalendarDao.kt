package uz.coder.muslimcalendar.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import uz.coder.muslimcalendar.data.db.model.MuslimCalendarDbModel
import uz.coder.muslimcalendar.domain.model.RefreshDay

@Dao
interface MuslimCalendarDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMuslimCalendar(list: List<MuslimCalendarDbModel>)

    @Query("delete from muslimCalendar")
    suspend fun deleteCalendar()

    @Query("select * from muslimCalendar where day=:day and month=:month and year=:year limit 1")
    fun presentDay(day:Int, month:Int, year:Int): Flow<MuslimCalendarDbModel?>

    @Query("select MAX(day) as day, month, year from muslimCalendar limit 1")
    fun refreshDay():Flow<RefreshDay>

    @Query("select * from muslimCalendar ORDER BY year ASC, month ASC, day ASC")
    fun oneMonth():Flow<List<MuslimCalendarDbModel>>

    // YANGI METOD: Eski ma'lumotlarni o'chirish
    @Query("DELETE FROM muslimCalendar WHERE NOT (day = :currentDay AND month = :currentMonth AND year = :currentYear)")
    suspend fun deleteOldData(currentDay: Int, currentMonth: Int, currentYear: Int)

    // YANA BIR VARIANT: Faqat oxirgi 30 kunni saqlash
    @Query("DELETE FROM muslimCalendar WHERE (year * 10000 + month * 100 + day) NOT IN (SELECT (year * 10000 + month * 100 + day) FROM muslimCalendar ORDER BY year DESC, month DESC, day DESC LIMIT 30)")
    suspend fun deleteExceptLast30Days()

    // YANGI METOD: Eng so'nggi ma'lumotni olish
    @Query("SELECT * FROM muslimCalendar ORDER BY year DESC, month DESC, day DESC LIMIT 1")
    suspend fun getLatest(): MuslimCalendarDbModel?
    @Query("""
        SELECT * FROM muslimCalendar 
        WHERE (year > :currentYear) OR (year = :currentYear AND month > :currentMonth) OR (year = :currentYear AND month = :currentMonth AND day >= :currentDay) 
        ORDER BY year, month, day
    """)
    fun fromTodayOnwards(currentDay: Int, currentMonth: Int, currentYear: Int): Flow<List<MuslimCalendarDbModel>>
}