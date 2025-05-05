package uz.coder.muslimcalendar.db

import android.app.Application
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import uz.coder.muslimcalendar.models.db.MuslimCalendarDbModel
import uz.coder.muslimcalendar.models.db.SuraDbModel

@Database([MuslimCalendarDbModel::class, SuraDbModel::class], version = 2)
abstract class AppDatabase:RoomDatabase() {
    abstract fun calendarDao():MuslimCalendarDao
    abstract fun suraDao():SuraDao
    companion object{
        private const val NAME = "main.db"
        private val LOCK = Any()
        private var calendarDatabase:AppDatabase? = null
        fun instance(context: Context):AppDatabase{
            calendarDatabase?.let {
                return it
            }
            synchronized(LOCK){
                calendarDatabase?.let {
                    return it
                }
            }
            val db = Room.databaseBuilder(context,AppDatabase::class.java, NAME).fallbackToDestructiveMigration(true).build()
            calendarDatabase = db
            return db
        }
    }
}