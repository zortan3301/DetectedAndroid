package com.devian.detected.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class LocalStorage {
    
    private static LocalStorage mInstance;
    private static final String APP_PREFERENCES = "APP_PREFERENCES";
    private static SharedPreferences mSharedPreferences;
    
    private LocalStorage(Activity activity) {
        mSharedPreferences =
                activity.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
    }
    
    public static LocalStorage getInstance(Activity activity) {
        if (mInstance == null) {
            mInstance = new LocalStorage(activity);
        }
        return mInstance;
    }
    
    public String getString(String key) {
        if (mSharedPreferences.contains(key)) {
            return mSharedPreferences.getString(key, "");
        } else {
            return null;
        }
    }
    
    public LocalStorage putString(String key, String value) {
        mSharedPreferences.edit().putString(key, value).apply();
        return this;
    }
    
}
