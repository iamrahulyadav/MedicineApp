package com.hvantage.medicineapp.util;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

import com.google.gson.JsonObject;
import com.hvantage.medicineapp.adapter.OfferPagerAdapter;
import com.hvantage.medicineapp.retrofit.ApiClient;
import com.hvantage.medicineapp.retrofit.MyApiEndpointInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SliderUtil {
    private static String TAG = "SliderUtil";

    public static void setSlider(final Context context, final ViewPager viewPagerOffers) {

        new AsyncTask<Void, String, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("method", AppConstants.METHODS.GET_SLIDER_IMAGES);
                Log.e(TAG, "GetSliderTask: Request >> " + jsonObject.toString());

                MyApiEndpointInterface apiService = ApiClient.getClient().create(MyApiEndpointInterface.class);
                Call<JsonObject> call = apiService.general(jsonObject);
                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        try {
                            Log.e(TAG, "GetSliderTask: Response >> " + response.body().toString());
                            String resp = response.body().toString();
                            JSONObject jsonObject = new JSONObject(resp);
                            if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                                ArrayList<String> list = new ArrayList<String>();
                                JSONArray jsonArray = jsonObject.getJSONArray("result");
                                AppPreferences.setSliderData(context, String.valueOf(jsonArray));
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    String data = jsonArray.getJSONObject(i).getString("slider_image");
                                    list.add(data);
                                }
                                slide(list, context, viewPagerOffers);
                                publishProgress("200");
                            } else {
                                publishProgress("400");
                            }
                        } catch (JSONException e) {
                            publishProgress("400");
                            Log.e(TAG, "GetSliderTask: onFailure: " + e.getMessage());
                        } catch (Exception e) {
                            publishProgress("400");
                            Log.e(TAG, "GetSliderTask: onFailure: " + e.getMessage());
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Log.e(TAG, "GetSliderTask: onFailure: " + t.getMessage());
                    }
                });
                return null;
            }

            @Override
            protected void onProgressUpdate(String... values) {
                super.onProgressUpdate(values);
                if (values[0].equalsIgnoreCase("200"))
                    viewPagerOffers.setVisibility(View.VISIBLE);
                else
                    viewPagerOffers.setVisibility(View.GONE);
            }
        }.execute();
    }

    private static void slide(ArrayList<String> list, Context context, ViewPager viewPagerOffers) {
        if (list.size() != 0) {
            viewPagerOffers.setVisibility(View.VISIBLE);
            viewPagerOffers.setAdapter(new OfferPagerAdapter(context, list));
        } else {
            viewPagerOffers.setVisibility(View.GONE);
        }
    }
}


