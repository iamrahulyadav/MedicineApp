package com.hvantage.medicineapp.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * Created by RK on 11-Jul-17.
 */

public class AppPreferences {

    public static final String PREFERENCES = "medicine";

    public static final String MOBILE_NO = "mobile_no";
    public static final String USER_ID = "user_id";
    public static final String USER_NAME = "user_name";
    public static final String EMAIL = "email";
    public static final String ORDER_TYPE = "order_type";
    public static final String SELECTED_PRES_ID = "selected_pres_id";
    public static final String SELECTED_ADD_ID = "selected_add_id";
    public static final String SLIDER_DATA = "slider_data";


    public static final String SELECTED_ADD = "selected_add";

    private static AppPreferences instance;
    private final SharedPreferences sharedPreferences;
    private final Editor editor;

    public AppPreferences(Context context) {
        instance = this;
        String prefsFile = context.getPackageName();
        sharedPreferences = context.getSharedPreferences(prefsFile, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static void setUserId(Context context, String value) {
        SharedPreferences preferences = context.getSharedPreferences(
                PREFERENCES, 0);
        Editor editor = preferences.edit();
        editor.putString(USER_ID, value);
        editor.commit();
    }

    public static String getUserId(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                PREFERENCES, 0);
        return pereference.getString(USER_ID, "");
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

    public static void setEmail(Context context, String value) {
        SharedPreferences preferences = context.getSharedPreferences(
                PREFERENCES, 0);
        Editor editor = preferences.edit();
        editor.putString(EMAIL, value);
        editor.commit();
    }

    public static String getEmail(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                PREFERENCES, 0);
        return pereference.getString(EMAIL, "");
    }

    public static void setUserName(Context context, String value) {
        SharedPreferences preferences = context.getSharedPreferences(
                PREFERENCES, 0);
        Editor editor = preferences.edit();
        editor.putString(USER_NAME, value);
        editor.commit();
    }

    public static String getUserName(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                PREFERENCES, 0);
        return pereference.getString(USER_NAME, "");
    }

    public static void setOrderType(Context context, int value) {
        SharedPreferences preferences = context.getSharedPreferences(
                PREFERENCES, 0);
        Editor editor = preferences.edit();
        editor.putInt(ORDER_TYPE, value);
        editor.commit();
    }

    public static int getOrderType(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                PREFERENCES, 0);
        return pereference.getInt(ORDER_TYPE, 0);
    }

    public static void setSelectedPresId(Context context, String value) {
        SharedPreferences preferences = context.getSharedPreferences(
                PREFERENCES, 0);
        Editor editor = preferences.edit();
        editor.putString(SELECTED_PRES_ID, value);
        editor.commit();
    }

    public static String getSelectedPresId(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                PREFERENCES, 0);
        return pereference.getString(SELECTED_PRES_ID, "");
    }

    public static void setSelectedAddId(Context context, String value) {
        SharedPreferences preferences = context.getSharedPreferences(
                PREFERENCES, 0);
        Editor editor = preferences.edit();
        editor.putString(SELECTED_ADD_ID, value);
        editor.commit();
    }

    public static String getSelectedAddId(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                PREFERENCES, 0);
        return pereference.getString(SELECTED_ADD_ID, "");
    }

    public static void setSelectedAdd(Context context, String value) {
        SharedPreferences preferences = context.getSharedPreferences(
                PREFERENCES, 0);
        Editor editor = preferences.edit();
        editor.putString(SELECTED_ADD, value);
        editor.commit();
    }

    public static String getSelectedAdd(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                PREFERENCES, 0);
        return pereference.getString(SELECTED_ADD, "");
    }

    public static void setSliderData(Context context, String value) {
        SharedPreferences preferences = context.getSharedPreferences(
                PREFERENCES, 0);
        Editor editor = preferences.edit();
        editor.putString(SLIDER_DATA, value);
        editor.commit();
    }

    public static String getSliderData(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                PREFERENCES, 0);
        return pereference.getString(SLIDER_DATA, "");
    }

    public static void clearPreference(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }
}
