package uz.coder.muslimcalendar.todo;

import android.content.Context;
import android.content.SharedPreferences;

import uz.coder.muslimcalendar.R;

public class SharedPref {
    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;
    private static SharedPref sharedPref;

    // Private constructor to enforce Singleton
    private SharedPref(Context context) {
        sharedPreferences = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static SharedPref getInstance(Context context) {
        if (sharedPref == null) {
            sharedPref = new SharedPref(context.getApplicationContext());
        }
        return sharedPref;
    }

    public <T> void saveValue(String key, T value) {
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
            throw new IllegalArgumentException("Unsupported type");
        }
        editor.apply();
    }

    public void removeValue(String key) {
        editor.remove(key).apply();
    }

    public void clear() {
        editor.clear().apply();
    }

    public String getString(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }

    public int getInt(String key, int defaultValue) {
        return sharedPreferences.getInt(key, defaultValue);
    }

    public boolean getBoolean(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    public float getFloat(String key) {
        return sharedPreferences.getFloat(key, 0f);
    }

    public long getLong(String key) {
        return sharedPreferences.getLong(key, 0L);
    }
}
