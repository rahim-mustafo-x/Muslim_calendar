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

    @Query("select * from muslimCalendar where day=:day and month=:month limit 1")
    fun presentDay(day:Int, month:Int): Flow<MuslimCalendarDbModel?>

    @Query("select MAX(day) as day, month from muslimCalendar limit 1")
    fun refreshDay():Flow<RefreshDay>

    @Query("select * from muslimCalendar")
    fun oneMonth():Flow<List<MuslimCalendarDbModel>>

    // YANGI METOD: Eski ma'lumotlarni o'chirish
    @Query("DELETE FROM muslimCalendar WHERE NOT (day = :currentDay AND month = :currentMonth)")
    suspend fun deleteOldData(currentDay: Int, currentMonth: Int)

    // YANA BIR VARIANT: Faqat oxirgi 30 kunni saqlash
    @Query("DELETE FROM muslimCalendar WHERE day NOT IN (SELECT day FROM muslimCalendar ORDER BY day DESC LIMIT 30)")
    suspend fun deleteExceptLast30Days()

    // YANGI METOD: Eng so'nggi ma'lumotni olish
    @Query("SELECT * FROM muslimCalendar ORDER BY day DESC LIMIT 1")
    suspend fun getLatest(): MuslimCalendarDbModel?
    @Query("""
        SELECT * FROM muslimCalendar 
        WHERE (month > :currentMonth) OR (month = :currentMonth AND day >= :currentDay) 
        ORDER BY month, day
    """)
    fun fromTodayOnwards(currentDay: Int, currentMonth: Int): Flow<List<MuslimCalendarDbModel>>
}