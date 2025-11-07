package uz.coder.muslimcalendar.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import uz.coder.muslimcalendar.data.db.model.AudioPathDbModel

@Dao
interface AudioPathDao {

    @Query("SELECT * FROM audioPath WHERE sura = :sura")
    fun getAudioPathBySura(sura: String): Flow<AudioPathDbModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAudioPath(audioPathDbModel: AudioPathDbModel)

    @Query("DELETE FROM audioPath")
    suspend fun deleteAll()
}
