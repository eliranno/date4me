package com.example.elirannoach.date4me.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceUtils {
    private static SharedPreferenceUtils mSharedPreferenceUtils;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mSharedPreferencesEditor = null;
    private Context mContext;

    private static final String PREFERENCE_FILE_NAME = "DATE4ME";
    public static final String DOB_TAG = "DOB";
    public static final String UID_TAG = "uid";
    public static final String GENDER = "gender";


    private SharedPreferenceUtils(Context context) {
        mContext = context;
        mSharedPreferences = context.getSharedPreferences(PREFERENCE_FILE_NAME,context.MODE_PRIVATE);
        mSharedPreferencesEditor = mSharedPreferences.edit();
    }


    public static synchronized SharedPreferenceUtils getInstance(Context context){
        if (mSharedPreferenceUtils ==null){
            mSharedPreferenceUtils = new SharedPreferenceUtils(context);
        }
        return mSharedPreferenceUtils;
    }

    public void setValue(String key,String value){
        mSharedPreferencesEditor.putString(key,value);
        mSharedPreferencesEditor.commit();
    }

    public void setValue(String key,int value){
        mSharedPreferencesEditor.putInt(key,value);
        mSharedPreferencesEditor.commit();
    }

    public String getStringValue(String key){
        return mSharedPreferences.getString(key,null);
    }

    public int getIntValue(String key){
        return mSharedPreferences.getInt(key,0);
    }
}
