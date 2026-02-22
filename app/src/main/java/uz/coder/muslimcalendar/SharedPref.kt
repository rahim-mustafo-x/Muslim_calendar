package uz.coder.muslimcalendar

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit

@Singleton
class SharedPref @Inject constructor(
    @ApplicationContext context: Context
) {
    private val sharedPreferences: SharedPreferences = context.applicationContext
        .getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)

    // Universal save method using reified generics
    internal inline fun <reified T> saveValue(key: String, value: T) {
        sharedPreferences.edit {
            when (value) {
                is String -> putString(key, value)
                is Int -> putInt(key, value)
                is Boolean -> putBoolean(key, value)
                is Float -> putFloat(key, value)
                is Long -> putLong(key, value)
                else -> throw IllegalArgumentException("Unsupported type: ${value!!::class.simpleName}")
            }
        }
    }

    // Remove one key
    fun removeValue(key: String) {
        sharedPreferences.edit { remove(key) }
    }

    // Clear all data
    fun clear() {
        sharedPreferences.edit { clear() }
    }

    // Getters with default values
    fun getString(key: String, defaultValue: String = ""): String =
        sharedPreferences.getString(key, defaultValue) ?: defaultValue

    fun getInt(key: String, defaultValue: Int = 0): Int =
        sharedPreferences.getInt(key, defaultValue)

    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean =
        sharedPreferences.getBoolean(key, defaultValue)

    fun getFloat(key: String, defaultValue: Float = 0f): Float =
        sharedPreferences.getFloat(key, defaultValue)

    fun getLong(key: String, defaultValue: Long = 0L): Long =
        sharedPreferences.getLong(key, defaultValue)
}
