package uz.coder.muslimcalendar.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import uz.coder.muslimcalendar.db.model.MuslimCalendarDbModel
import uz.coder.muslimcalendar.domain.model.RefreshDay

@Dao
interface MuslimCalendarDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMuslimCalendar(list: List<MuslimCalendarDbModel>)

    @Query("delete from muslimCalendar")
    suspend fun deleteCalendar()

    @Query("select * from muslimCalendar where day=:day and month=:month limit 1")
    fun presentDay(day:Int, month:Int): Flow<MuslimCalendarDbModel>

    @Query("select MAX(day) as day, month from muslimCalendar limit 1")
    fun refreshDay():Flow<RefreshDay>

    @Query("select * from muslimCalendar")
    fun oneMonth():Flow<List<MuslimCalendarDbModel>>
}