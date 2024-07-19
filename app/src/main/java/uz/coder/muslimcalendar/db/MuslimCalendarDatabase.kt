package uz.coder.muslimcalendar.db

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import uz.coder.muslimcalendar.models.db.MuslimCalendarDbModel

@Database([MuslimCalendarDbModel::class], version = 1)
abstract class MuslimCalendarDatabase:RoomDatabase() {
    abstract fun calendarDao():MuslimCalendarDao
    companion object{
        private const val NAME = "main.db"
        private val LOCK = Any()
        private var calendarDatabase:MuslimCalendarDatabase? = null
        fun instance(application: Application):MuslimCalendarDatabase{
            calendarDatabase?.let {
                return it
            }
            synchronized(LOCK){
                calendarDatabase?.let {
                    return it
                }
            }
            val db = Room.databaseBuilder(application,MuslimCalendarDatabase::class.java, NAME).fallbackToDestructiveMigration().build()
            calendarDatabase = db
            return db
        }
    }
}