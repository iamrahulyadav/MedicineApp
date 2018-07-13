package com.hvantage.medicineapp.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * Created by Peter on 11-Jul-17.
 */

public class AppPreferences {

    public static final String PREFERENCES = "my_home_check";

    public static final String MOBILE_NO = "mobile_no";

    private static AppPreferences instance;
    private final SharedPreferences sharedPreferences;
    private final Editor editor;

    public AppPreferences(Context context) {
        instance = this;
        String prefsFile = context.getPackageName();
        sharedPreferences = context.getSharedPreferences(prefsFile, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static void setMobileNo(Context context, String value) {
        SharedPreferences preferences = context.getSharedPreferences(
                PREFERENCES, 0);
        Editor editor = preferences.edit();
        editor.putString(MOBILE_NO, value);
        editor.commit();
    }

    public static String getMobileNo(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                PREFERENCES, 0);
        return pereference.getString(MOBILE_NO, "");
    }
}
