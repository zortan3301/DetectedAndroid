package com.devian.detected.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.devian.detected.utils.Network.NetworkService;


public class LocalSharedPreferences {
    
    private static LocalSharedPreferences mInstance;
    private static final String APP_PREFERENCES = "APP_PREFERENCES";
    private static SharedPreferences mSharedPreferences;
    
    public LocalSharedPreferences(Activity activity) {
        mSharedPreferences =
                activity.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
    }
    
    public static LocalSharedPreferences getInstance(Activity activity) {
        if (mInstance == null) {
            mInstance = new LocalSharedPreferences(activity);
        }
        return mInstance;
    }
    
    public String getString(String key) {
        return mSharedPreferences.getString(key, "");
    }
    
    public LocalSharedPreferences putString(String key, String value) {
        mSharedPreferences.edit().putString(key, value).apply();
        return this;
    }
    
    public void updateServerKey(String key) {
        putString("serverKey", key);
    }
    
    public String getServerKey() {
        return getString("serverKey");
    }
    
    public void updateUUID(String UUID) {
        putString("uuid", UUID);
    }
    
    public String getUUID() {
        return getString("uuid");
    }
    
}
