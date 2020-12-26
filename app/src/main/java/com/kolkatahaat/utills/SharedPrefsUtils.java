package com.kolkatahaat.utills;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefsUtils {
    private static final String APP_SETTINGS = "APP_SETTINGS";


    // properties
    private static final String SOME_STRING_VALUE = "SOME_STRING_VALUE";
    // other properties...


    private SharedPrefsUtils() {}

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(APP_SETTINGS, Context.MODE_PRIVATE);
    }

    public static String getSomeStringValue(Context context) {
        return getSharedPreferences(context).getString(SOME_STRING_VALUE , null);
    }

    public static void setSomeStringValue(Context context, String newValue) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(SOME_STRING_VALUE , newValue);
        editor.commit();
    }


    //SharedPref.init(getApplicationContext());
}
