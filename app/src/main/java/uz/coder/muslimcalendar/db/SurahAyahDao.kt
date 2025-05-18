package uz.coder.muslimcalendar.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import uz.coder.muslimcalendar.db.model.SurahAyahDbModel

@Dao
interface SurahAyahDao{
    @Query("SELECT * FROM surah_list WHERE sura=:sura")
    fun getSurahAyahsById(sura: String): Flow<List<SurahAyahDbModel>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(suraDbModels: List<SurahAyahDbModel>)
    @Query("DELETE FROM surah_list")
    suspend fun deleteAll()

}