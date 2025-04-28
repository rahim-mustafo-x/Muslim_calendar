package uz.coder.muslimcalendar.db

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import uz.coder.muslimcalendar.models.db.MuslimCalendarDbModel
import uz.coder.muslimcalendar.models.db.SuraDbModel

@Database([MuslimCalendarDbModel::class, SuraDbModel::class], version = 1)
abstract class ApDatabase:RoomDatabase() {
    abstract fun calendarDao():MuslimCalendarDao
    abstract fun suraDao():SuraDao
    companion object{
        private const val NAME = "main.db"
        private val LOCK = Any()
        private var calendarDatabase:ApDatabase? = null
        fun instance(application: Application):ApDatabase{
            calendarDatabase?.let {
                return it
            }
            synchronized(LOCK){
                calendarDatabase?.let {
                    return it
                }
            }
            val db = Room.databaseBuilder(application,ApDatabase::class.java, NAME).build()
            calendarDatabase = db
            return db
        }
    }
}