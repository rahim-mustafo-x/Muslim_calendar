package uz.coder.muslimcalendar.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import uz.coder.muslimcalendar.data.db.model.SurahAyahDbModel

@Dao
interface SurahAyahDao{
    @Query("SELECT * FROM surahAyah WHERE sura=:sura")
    fun getSurahAyahsById(sura: String): Flow<List<SurahAyahDbModel>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(suraDbModels: List<SurahAyahDbModel>)
    @Query("DELETE FROM surahAyah")
    suspend fun deleteAll()

}