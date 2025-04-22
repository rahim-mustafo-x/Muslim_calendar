package uz.coder.muslimcalendar.todo;

import android.content.Context;
import android.content.SharedPreferences;
import uz.coder.muslimcalendar.R;

public class SharedPref {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static SharedPref sharedPref;
    private SharedPref(){}

    public static SharedPref getInstance(Context context){
        if (sharedPref == null){
            sharedPref = new SharedPref();
            sharedPref.sharedPreferences = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
        }
        return sharedPref;
    }
    public <T> void saveValue(String key, T value){
        if (value instanceof String){
            sharedPref.editor.putString(key, String.valueOf(value)).apply();
        }else if (value instanceof Integer){
            sharedPref.editor.putInt(key, (Integer) value).apply();
        }else if (value instanceof Boolean){
            sharedPref.editor.putBoolean(key, (Boolean) value).apply();
        }else if (value instanceof Float){
            sharedPref.editor.putFloat(key, (Float) value).apply();
        }
        else if (value instanceof Long){
            sharedPref.editor.putLong(key, (Long) value).apply();
        }else {
            throw new RuntimeException("Unsupported type");
        }
    }
    public void removeValue(String key){
        sharedPref.editor.remove(key).apply();
    }
    public void clear(){
        sharedPref.editor.clear().apply();
    }
    public String getString(String key, String defaultValue){
        return sharedPref.sharedPreferences.getString(key, defaultValue);
    }
    public int getInt(String key){
        return sharedPref.sharedPreferences.getInt(key, 0);
    }
    public boolean getBoolean(String key){
        return sharedPref.sharedPreferences.getBoolean(key, false);
    }
    public float getFloat(String key){
        return sharedPref.sharedPreferences.getFloat(key, 0);
    }
    public long getLong(String key){
        return sharedPref.sharedPreferences.getLong(key, 0);
    }
}
