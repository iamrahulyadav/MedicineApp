package com.hvantage.medicineapp.retrofit;

import com.google.gson.JsonObject;
import com.hvantage.medicineapp.util.AppConstants;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by RK on 2018-02-26.
 */

public interface MyApiEndpointInterface {
    @POST(AppConstants.ENDPOINT.REGISTER)
    Call<JsonObject> user_register(@Body JsonObject jsonObject);

    @POST(AppConstants.ENDPOINT.PRODUCT)
    Call<JsonObject> products(@Body JsonObject jsonObject);

}
