package uz.coder.muslimcalendar;

import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;

@Singleton
public class SharedPref {

    private final SharedPreferences sharedPreferences;

    @Inject
    public SharedPref(@ApplicationContext Context context) {
        sharedPreferences = context.getApplicationContext()
                .getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
    }

    // ---- Universal save method ----
    public <T> void saveValue(String key, T value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        } else {
            throw new IllegalArgumentException("Unsupported type: " + value.getClass().getSimpleName());
        }
        editor.apply();
    }

    // ---- Remove one key ----
    public void removeValue(String key) {
        sharedPreferences.edit().remove(key).apply();
    }

    // ---- Clear all data ----
    public void clear() {
        sharedPreferences.edit().clear().apply();
    }

    // ---- Getters ----
    public String getString(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }

    public int getInt(String key, int defaultValue) {
        return sharedPreferences.getInt(key, defaultValue);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    public float getFloat(String key, float defaultValue) {
        return sharedPreferences.getFloat(key, defaultValue);
    }

    public long getLong(String key, long defaultValue) {
        return sharedPreferences.getLong(key, defaultValue);
    }
}