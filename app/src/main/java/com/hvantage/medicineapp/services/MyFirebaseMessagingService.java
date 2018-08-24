package com.hvantage.medicineapp.services;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;
import com.hvantage.medicineapp.R;
import com.hvantage.medicineapp.activity.MainActivity;
import com.hvantage.medicineapp.util.AppConstants;

import org.json.JSONException;
import org.json.JSONObject;

public class MyFirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    private static final String TAG = "FirebaseInstanceService";
    private String refreshedToken = "";

    @Override
    public void onNewToken(String mToken) {
        super.onNewToken(mToken);
        Log.e("TOKEN", mToken);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.e("Notification Data : ", remoteMessage.getData().toString());
        if (remoteMessage.getData().size() > 0) {
            String ORDER_PLACED = "" + remoteMessage.getData().get(AppConstants.NOTIFICATION_KEY.ORDER_PLACED);
            String ORDER_UPDATED = "" + remoteMessage.getData().get(AppConstants.NOTIFICATION_KEY.ORDER_UPDATED);

            if (!ORDER_PLACED.equalsIgnoreCase("null")) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(ORDER_PLACED);
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    sendNotification(intent, jsonObject.getString("msg"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (!ORDER_UPDATED.equalsIgnoreCase("null")) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(ORDER_UPDATED);
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    sendNotification(intent, jsonObject.getString("msg"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }


    }

    private void sendNotification(Intent intent, String message) {
        try {
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent,
                    PendingIntent.FLAG_ONE_SHOT);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(getResources().getString(R.string.app_name))
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher))
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(AppConstants.NOTIFICATION_ID.ORDER_PLACED_ID, notificationBuilder.build());
        } catch (Exception e) {
            Log.e("Notification Ex", e.getMessage());
        }
    }
}
/*
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.e(TAG, "Refreshed token: " + refreshedToken);
        if (!AppPreferences.getUserId(getApplicationContext()).equalsIgnoreCase("")) {
//            updateFCM();
        } else {

        }
    }*/

//    void updateFCM() {
//        JsonObject jsonObject = new JsonObject();
//        jsonObject.addProperty(AppConstants.KEYS.METHOD, AppConstants.FEILDEXECUTATIVE.UPDATE_FCM_TOKEN);
//        jsonObject.addProperty(AppConstants.KEYS.USER_ID, AppPreference.getUserId(MyFirebaseInstanceIDService.this));
//        jsonObject.addProperty(AppConstants.KEYS.FCM_ID, refreshedToken);
//        Log.e(TAG, "Request UDPATE_FCM >> " + jsonObject.toString());
//
//        MyApiEndpointInterface apiService = ApiClient.getClient().create(MyApiEndpointInterface.class);
//        Call<JsonObject> call = apiService.register_log_api(jsonObject);
//        call.enqueue(new Callback<JsonObject>() {
//            @Override
//            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//                Log.e(TAG, "Response UDPATE_FCM >> " + response.body().toString());
//                JsonObject jsonObject = response.body();
//                if (jsonObject.get("status").getAsString().equals("200")) {
//                } else {
//                }
//            }
//
//            @Override
//            public void onFailure(Call<JsonObject> call, Throwable t) {
//                Log.e(TAG, "onFailure: " + t.getMessage());
//            }
//        });
//    }

