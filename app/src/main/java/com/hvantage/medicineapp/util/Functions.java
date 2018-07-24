package com.hvantage.medicineapp.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Hvantage2 on 2018-02-26.
 */

public class Functions {
    public static EditText EditTextPointer;
    public static String errorMessage;

    public static void hideSoftKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static boolean isEmailValid(EditText tv) {
        //add your own logic
        if (TextUtils.isEmpty(tv.getText())) {
            EditTextPointer = tv;
            errorMessage = "Enter email address";
            return false;
        } else {
            if (android.util.Patterns.EMAIL_ADDRESS.matcher(tv.getText()).matches()) {
                return true;
            } else {
                EditTextPointer = tv;
                errorMessage = "Invalid email";
                return false;
            }
        }
    }

    public static final boolean isValidPhoneNumber(String mobno) {
        Boolean isValid = false;
        if (mobno.trim().length() < 10) {
            isValid = false;
        }
        if (mobno.trim().length() == 10) {
            Pattern pattern;
            Matcher matcher;
            final String MOBILE_PATTERN = "^[7-9][0-9]{9}$";
            pattern = Pattern.compile(MOBILE_PATTERN);
            matcher = pattern.matcher(mobno);
            boolean isMatch = matcher.matches();
            if (isMatch) {
                isValid = true;
            } else {
                isValid = false;
            }
        }
        return isValid;
    }

    public static String getCurrentDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String strDate = sdf.format(c.getTime());
        return strDate;
    }

    public static String getCurrentTime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        String strDate = sdf.format(c.getTime());
        return strDate;
    }

    public static boolean isConnectingToInternet(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
        }
        return false;
    }

    public static String loadJSONFromAsset(Context context, String assetName) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(assetName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public static Bitmap base64ToBitmap(String base64) {
        byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return bitmap;
    }

    public static void showSettingsAlert(final Activity mContext) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("GPS is disabled");
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Enable Location", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });
        // on pressing cancel button
        alertDialog.setNegativeButton("Go Back", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                mContext.finish();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    public static double roundTwoDecimals(double d) {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Double.valueOf(twoDForm.format(d));
    }


}
