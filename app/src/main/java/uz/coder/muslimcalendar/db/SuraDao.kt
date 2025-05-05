package uz.coder.muslimcalendar.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import uz.coder.muslimcalendar.models.db.SuraDbModel

@Dao
interface SuraDao{
    @Query("SELECT * FROM sura")
    fun getAllSura(): Flow<List<SuraDbModel>>
    @Query("SELECT * FROM sura WHERE number=:number")
    fun getSuraById(number: Int): Flow<SuraDbModel>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(suraDbModels: List<SuraDbModel>)
    @Query("DELETE FROM sura")
    suspend fun deleteAll()
}