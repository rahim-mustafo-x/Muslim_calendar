package uz.coder.muslimcalendar.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import uz.coder.muslimcalendar.data.db.model.MuslimCalendarDbModel

@Dao
interface MuslimCalendarDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMuslimCalendar(
        list: List<MuslimCalendarDbModel>
    )

    @Query("DELETE FROM muslimCalendar")
    suspend fun deleteCalendar()

    @Query(
        """
        SELECT * FROM muslimCalendar
        WHERE day = :day
          AND month = :month
          AND year = :year
        LIMIT 1
        """
    )
    fun presentDay(
        day: Int,
        month: Int,
        year: Int
    ): Flow<MuslimCalendarDbModel?>

    @Query(
        """
        SELECT * FROM muslimCalendar
        ORDER BY year ASC, month ASC, day ASC
        """
    )
    fun oneMonth(): Flow<List<MuslimCalendarDbModel>>

    @Query(
        """
        SELECT * FROM muslimCalendar
        ORDER BY year DESC, month DESC, day DESC
        LIMIT 1
        """
    )
    suspend fun getLatest(): MuslimCalendarDbModel?

    @Query("SELECT COUNT(*) FROM muslimCalendar WHERE month = :month AND year = :year")
    suspend fun countInMonth(month: Int, year: Int): Int

    @Query(
        """
        SELECT * FROM muslimCalendar
        WHERE
            year > :currentYear
            OR (
                year = :currentYear
                AND month > :currentMonth
            )
            OR (
                year = :currentYear
                AND month = :currentMonth
                AND day >= :currentDay
            )
        ORDER BY year ASC, month ASC, day ASC
        """
    )
    fun fromTodayOnwards(
        currentDay: Int,
        currentMonth: Int,
        currentYear: Int
    ): Flow<List<MuslimCalendarDbModel>>
}
