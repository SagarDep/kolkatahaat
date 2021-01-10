package com.kolkatahaat.utills;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.lang.ref.WeakReference;

public class SharedPrefsUtils {
    private static final String APP_SETTINGS = "APP_SETTINGS";

    public static final String USER_DETAIL = "User_Detail";

    public static final String USER_INCOME = "USER_INCOME";
    public static final String USER_MARITAL_STATUS = "USER_MARITAL_STATUS";
    public static final String USER_SALARY_FLOAT= "USER_SALARY_FLOAT";
    public static final String USER_SALARY_LONG= "USER_SALARY_LONG";
    public static final String USER_AGE= "USER_AGE";

    /**
     * Called to save supplied value in shared preferences against given key.
     *
     * @param context Context of caller activity
     * @param key     Key of value to save against
     * @param value   Value to save
     */
    public static void saveToPrefs(Context context, String key, Object value) {
        WeakReference<Context> contextWeakReference = new WeakReference<Context>(context);
        if (contextWeakReference.get() != null) {
            SharedPreferences prefs =
                    PreferenceManager.getDefaultSharedPreferences(contextWeakReference.get());
            final SharedPreferences.Editor editor = prefs.edit();
            if (value instanceof Integer) {
                editor.putInt(key, ((Integer) value).intValue());
            } else if (value instanceof String) {
                editor.putString(key, value.toString());
            } else if (value instanceof Boolean) {
                editor.putBoolean(key, ((Boolean) value).booleanValue());
            } else if (value instanceof Long) {
                editor.putLong(key, ((Long) value).longValue());
            } else if (value instanceof Float) {
                editor.putFloat(key, ((Float) value).floatValue());
            } else if (value instanceof Double) {
                editor.putLong(key, Double.doubleToRawLongBits((double) value));
            }
            editor.commit();
        }
    }

    /**
     * Called to retrieve required value from shared preferences, identified by given key.
     * Default value will be returned of no value found or error occurred.
     *
     * @param context      Context of caller activity
     * @param key          Key to find value against
     * @param defaultValue Value to return if no data found against given key
     * @return Return the value found against given key, default if not found or any error occurs
     */
    public static Object getFromPrefs(Context context, String key, Object defaultValue) {
        WeakReference<Context> contextWeakReference = new WeakReference<Context>(context);
        if (contextWeakReference.get() != null) {
            SharedPreferences sharedPrefs =
                    PreferenceManager.getDefaultSharedPreferences(contextWeakReference.get());
            try {
                if (defaultValue instanceof String) {
                    return sharedPrefs.getString(key, defaultValue.toString());
                } else if (defaultValue instanceof Integer) {
                    return sharedPrefs.getInt(key, ((Integer) defaultValue).intValue());
                } else if (defaultValue instanceof Boolean) {
                    return sharedPrefs.getBoolean(key, ((Boolean) defaultValue).booleanValue());
                } else if (defaultValue instanceof Long) {
                    return sharedPrefs.getLong(key, ((Long) defaultValue).longValue());
                } else if (defaultValue instanceof Float) {
                    return sharedPrefs.getFloat(key, ((Float) defaultValue).floatValue());
                } else if (defaultValue instanceof Double) {
                    return Double.longBitsToDouble(sharedPrefs.getLong(key, Double.doubleToLongBits((double) defaultValue)));
                }
            } catch (Exception e) {
                Log.e("Execption", e.getMessage());
                return defaultValue;
            }
        }
        return defaultValue;
    }

    /**
     * @param context Context of caller activity
     * @param key     Key to delete from SharedPreferences
     */
    public static void removeFromPrefs(Context context, String key) {
        WeakReference<Context> contextWeakReference = new WeakReference<Context>(context);
        if (contextWeakReference.get() != null) {
            SharedPreferences prefs =
                    PreferenceManager.getDefaultSharedPreferences(contextWeakReference.get());
            final SharedPreferences.Editor editor = prefs.edit();
            editor.remove(key);
            editor.commit();
        }
    }

    public static boolean hasKey(Context context, String key) {
        WeakReference<Context> contextWeakReference = new WeakReference<Context>(context);
        if (contextWeakReference.get() != null) {
            SharedPreferences prefs =
                    PreferenceManager.getDefaultSharedPreferences(contextWeakReference.get());
            return prefs.contains(key);
        }
        return false;
    }


    /*

    PrefUtils.saveToPrefs(this,PrefKeys.USER_AGE,10);
        PrefUtils.saveToPrefs(this,PrefKeys.USER_MARITAL_STATUS,false);
        PrefUtils.saveToPrefs(this,PrefKeys.USER_SALARY_FLOAT,2.4);
        PrefUtils.saveToPrefs(this,PrefKeys.USER_SALARY_LONG,1287192837);
        PrefUtils.saveToPrefs(this,PrefKeys.USER_INCOME,2398742);

        //Getting data from preference
        int age = (Integer) PrefUtils.getFromPrefs(this, PrefKeys.USER_AGE, new Integer(10));
        boolean isMarried = (Boolean) PrefUtils.getFromPrefs(this, PrefKeys.USER_MARITAL_STATUS, false);
        float salaryFloat  = (Float) PrefUtils.getFromPrefs(this, PrefKeys.USER_SALARY_FLOAT, new Float(2.4));
        float salaryLong  = (Float) PrefUtils.getFromPrefs(this, PrefKeys.USER_SALARY_LONG, new Long(239847));
        double income = (Double) PrefUtils.getFromPrefs(this, PrefKeys.USER_INCOME, new Double(10));


        //Remove a value from preference
        PrefUtils.removeFromPrefs(this, PrefKeys.USER_INCOME);

        //Check whether a key is present in the preference or not
        boolean hasGender = PrefUtils.hasKey(this, PrefKeys.USER_GENDER);

    */
}
