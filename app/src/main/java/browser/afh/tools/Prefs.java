package browser.afh.tools;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class Prefs {
    private final SharedPreferences preferences;
    private final SharedPreferences.Editor editor;
    public Prefs(Context context){
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();
    }

    public void put(String prefName, String data){
        editor.putString(prefName, data);
        editor.apply();
    }

    public void put(String prefName, boolean data){
        editor.putBoolean(prefName, data);
        editor.apply();
    }

    public void put(String prefName, int data){
        editor.putInt(prefName, data);
        editor.apply();
    }

    public void put(String prefName, float data){
        editor.putFloat(prefName, data);
        editor.apply();
    }

    public boolean get(String prefName, boolean defaultValue){
        return preferences.getBoolean(prefName, defaultValue);
    }

    public String get(String prefName, String defaultValue){
        return preferences.getString(prefName, defaultValue);
    }
}
