package com.mubo.socialapp.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Ayarlar {
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    public static int camIdVideo = 0;
    public static int camIdPhoto = 0;

    public Ayarlar(Context cx) {
        preferences = PreferenceManager.getDefaultSharedPreferences(cx.getApplicationContext());
        editor = preferences.edit();
    }
    public boolean set_pref_int(String prefName,int data) {
        editor.putInt(prefName, data);
        editor.commit();
        return true;
    }
    public boolean set_pref_string(String prefName,String data) {
        editor.putString(prefName, data);
        editor.commit();
        return true;
    }
    public boolean set_pref_long(String prefName,Long data) {
        editor.putLong(prefName, data);
        editor.commit();
        return true;
    }
    public boolean set_pref_float(String prefName,Float data) {
        editor.putFloat(prefName, data);
        editor.commit();
        return true;
    }
    public boolean set_pref_bool(String prefName,boolean data) {
        editor.putBoolean(prefName, data);
        editor.commit();
        return true;
    }
    public int get_pref_int(String prefName){
        return preferences.getInt(prefName,-1);
    }
    public String get_pref_string(String prefName){
        return preferences.getString(prefName, "");
    }
    public Long get_pref_long(String prefName){
        return preferences.getLong(prefName, -1);
    }
    public float get_pref_float(String prefName){
        return preferences.getFloat(prefName, -1.0f);
    }
    public boolean get_pref_bool(String prefName){
        return preferences.getBoolean(prefName, false);
    }
    public boolean get_pref_bool_ble(String prefName){
        return preferences.getBoolean(prefName, true);
    }
    public boolean get_sw_bool(String prefName){
        return preferences.getBoolean(prefName, true);
    }
    public boolean remove_pref(String prefName){
        editor.remove(prefName);
        editor.commit();
        return true;
    }
}
