package com.hvantage.medicineapp.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hvantage.medicineapp.R;
import com.hvantage.medicineapp.adapter.DailyNeedProductAdapter;
import com.hvantage.medicineapp.adapter.SpinnerCatAdapter;
import com.hvantage.medicineapp.adapter.SpinnerSubcatAdapter;
import com.hvantage.medicineapp.database.DBHelper;
import com.hvantage.medicineapp.model.CartData;
import com.hvantage.medicineapp.model.CategoryData;
import com.hvantage.medicineapp.model.ProductData;
import com.hvantage.medicineapp.model.SubCategoryData;
import com.hvantage.medicineapp.retrofit.ApiClient;
import com.hvantage.medicineapp.retrofit.MyApiEndpointInterface;
import com.hvantage.medicineapp.util.AppConstants;
import com.hvantage.medicineapp.util.Functions;
import com.hvantage.medicineapp.util.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MoreProductsActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MoreProductsActivity";
    ArrayList<ProductData> productList;
    private Context context;
    private RecyclerView recylcer_view_daily;
    private DailyNeedProductAdapter productAdapter;
    private ProgressBar progressBar;
    private TextView textCartItemCount;
    private int mCartItemCount = 0;
    private ArrayList<CategoryData> catList;
    private ArrayList<SubCategoryData> subcatList;
    private AppCompatSpinner spinnerCat, spinnerSubcat;
    private SpinnerCatAdapter adapterCat;
    private String selectedCatId = "0";
    private String selectedSubcatId = "0";
    private SpinnerSubcatAdapter adapterSubcat;
    private AppCompatTextView tvApply;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_products);
        context = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        productList = new ArrayList<ProductData>();
        catList = new ArrayList<CategoryData>();
        subcatList = new ArrayList<SubCategoryData>();
        init();
        setProductAdapter();
        setCatAdapter();
        if (Functions.isConnectingToInternet(context)) {
            new CategoryTask().execute();
            new ProductTask().execute();
        } else {
            Toast.makeText(context, getResources().getString(R.string.no_internet_text), Toast.LENGTH_SHORT).show();
        }
    }

    private void init() {
        recylcer_view_daily = (RecyclerView) findViewById(R.id.recylcer_view_daily);
        tvApply = (AppCompatTextView) findViewById(R.id.tvApply);
        spinnerCat = (AppCompatSpinner) findViewById(R.id.spinnerCat);
        spinnerSubcat = (AppCompatSpinner) findViewById(R.id.spinnerSubcat);
        tvApply.setOnClickListener(this);
    }

    private void setProductAdapter() {
        productAdapter = new DailyNeedProductAdapter(context, productList);
        recylcer_view_daily.setLayoutManager(new LinearLayoutManager(context));
        recylcer_view_daily.setAdapter(productAdapter);
        productAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tvApply) {
            if (selectedSubcatId.equalsIgnoreCase("") || selectedSubcatId.equalsIgnoreCase("0"))
                Toast.makeText(context, "Select Filters", Toast.LENGTH_SHORT).show();
            else {
                new ProductTaskCat().execute();
            }
        }
    }

    class ProductTask extends AsyncTask<Void, String, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("method", AppConstants.METHODS.GET_DAILY_NEED_PRODUCTS);
            Log.e(TAG, "ProductTask: Request >> " + jsonObject.toString());

            MyApiEndpointInterface apiService = ApiClient.getClient().create(MyApiEndpointInterface.class);
            Call<JsonObject> call = apiService.products(jsonObject);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    productList.clear();
                    Log.e(TAG, "ProductTask: Response >> " + response.body().toString());
                    String resp = response.body().toString();
                    try {
                        JSONObject jsonObject = new JSONObject(resp);
                        if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("result");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                Gson gson = new Gson();
                                ProductData data = gson.fromJson(jsonArray.getJSONObject(i).toString(), ProductData.class);
                                productList.add(data);
                                if (i == 10) {
                                    break;
                                }
                            }
                            publishProgress("200", "");
                        } else if (jsonObject.getString("status").equalsIgnoreCase("400")) {
                            String msg = jsonObject.getJSONArray("result").getJSONObject(0).getString("msg");
                            publishProgress("400", msg);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        publishProgress("400", getResources().getString(R.string.api_error_msg));
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    publishProgress("400", getResources().getString(R.string.api_error_msg));
                }
            });
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            hideProgressDialog();
            productAdapter.notifyDataSetChanged();
            String status = values[0];
            String msg = values[1];
            if (status.equalsIgnoreCase("400")) {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void showProgressDialog() {
        progressBar = ProgressBar.show(context, "Processing...", true, false, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                // TODO Auto-generated method stub
            }
        });
    }

    private void hideProgressDialog() {
        if (progressBar != null)
            progressBar.dismiss();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        else if (item.getItemId() == R.id.action_search) {
            startActivity(new Intent(context, SearchActivity.class));
        } else if (item.getItemId() == R.id.action_cart) {
            startActivity(new Intent(context, CartActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        final MenuItem menuItem = menu.findItem(R.id.action_cart);
        MenuItem menuSearch = menu.findItem(R.id.action_search);
        menuSearch.setVisible(true);

        View actionView = MenuItemCompat.getActionView(menuItem);
        textCartItemCount = (TextView) actionView.findViewById(R.id.cart_badge);


        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(menuItem);
            }
        });
        setupBadge();

        return true;
    }

    private void setupBadge() {
        if (textCartItemCount != null) {
            ArrayList<CartData> list = new DBHelper(context).getCartData();
            if (list != null) {
                Log.e(TAG, "setupBadge: list.size() >> " + list.size());
                mCartItemCount = list.size();
                if (mCartItemCount > 0) {
                    textCartItemCount.setText(String.valueOf(mCartItemCount));
                    textCartItemCount.setVisibility(View.VISIBLE);
                } else
                    textCartItemCount.setVisibility(View.GONE);
            } else {
                mCartItemCount = 0;
                textCartItemCount.setVisibility(View.GONE);
            }
        }
    }

    class CategoryTask extends AsyncTask<Void, String, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("method", AppConstants.METHODS.GET_ALL_CATEGORIES);
            Log.e(TAG, "CategoryTask: Request >> " + jsonObject.toString());

            MyApiEndpointInterface apiService = ApiClient.getClient().create(MyApiEndpointInterface.class);
            Call<JsonObject> call = apiService.products(jsonObject);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    try {
                        Log.e(TAG, "CategoryTask: Response >> " + response.body().toString());
                        String resp = response.body().toString();
                        catList.clear();
                        JSONObject jsonObject = new JSONObject(resp);
                        if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("result");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                Gson gson = new Gson();
                                CategoryData data = gson.fromJson(jsonArray.getJSONObject(i).toString(), CategoryData.class);
                                catList.add(data);
                            }
                            publishProgress("200", "");
                        } else if (jsonObject.getString("status").equalsIgnoreCase("400")) {
                            String msg = jsonObject.getJSONArray("result").getJSONObject(0).getString("msg");
                            publishProgress("400", msg);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        publishProgress("400", getResources().getString(R.string.api_error_msg));
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    publishProgress("400", getResources().getString(R.string.api_error_msg));
                }
            });
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            adapterCat.notifyDataSetChanged();
            String status = values[0];
            String msg = values[1];
            if (status.equalsIgnoreCase("400")) {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void setCatAdapter() {
        adapterCat = new SpinnerCatAdapter(MoreProductsActivity.this, R.layout.spinner_cat_item_layout, R.id.tvTitle, catList);
        spinnerCat.setAdapter(adapterCat);
        spinnerCat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                selectedCatId = catList.get(position).getCatId();
                Log.e(TAG, "onItemSelected: selectedCatId >> " + selectedCatId);
                new SubcategoryTask().execute();
                setSubCatAdapter();
//                ((TextView) spinnerCat.getSelectedView().findViewById(R.id.tvTitle)).setTextColor(getResources().getColor(R.color.hintcolor));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void setSubCatAdapter() {
        adapterSubcat = new SpinnerSubcatAdapter(MoreProductsActivity.this, R.layout.spinner_cat_item_layout, R.id.tvTitle, subcatList);
        spinnerSubcat.setAdapter(adapterSubcat);
        spinnerSubcat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                selectedSubcatId = subcatList.get(position).getSubCatId();
                Log.e(TAG, "onItemSelected: selectedCatId >> " + selectedSubcatId);
                Log.e(TAG, "onItemSelected: selectedCatId >> " + selectedCatId);
                //new ProductTaskCat().execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    class SubcategoryTask extends AsyncTask<Void, String, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("method", AppConstants.METHODS.GET_ALL_SUBCATEGORIES);
            jsonObject.addProperty("cat_id", selectedCatId);
            Log.e(TAG, "SubcategoryTask: Request >> " + jsonObject.toString());

            MyApiEndpointInterface apiService = ApiClient.getClient().create(MyApiEndpointInterface.class);
            Call<JsonObject> call = apiService.products(jsonObject);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.e(TAG, "SubcategoryTask: Response >> " + response.body().toString());
                    String resp = response.body().toString();
                    subcatList.clear();
                    try {
                        JSONObject jsonObject = new JSONObject(resp);
                        if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("result");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                Gson gson = new Gson();
                                SubCategoryData data = gson.fromJson(jsonArray.getJSONObject(i).toString(), SubCategoryData.class);
                                subcatList.add(data);
                            }
                            publishProgress("200", "");
                        } else if (jsonObject.getString("status").equalsIgnoreCase("400")) {
                            String msg = jsonObject.getJSONArray("result").getJSONObject(0).getString("msg");
                            publishProgress("400", msg);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        publishProgress("400", getResources().getString(R.string.api_error_msg));
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    publishProgress("400", getResources().getString(R.string.api_error_msg));
                }
            });
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            adapterSubcat.notifyDataSetChanged();
            String status = values[0];
            String msg = values[1];
            if (status.equalsIgnoreCase("400")) {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        }
    }

    class ProductTaskCat extends AsyncTask<Void, String, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("method", AppConstants.METHODS.GET_SUBCAT_PRODUCTS);
            jsonObject.addProperty("sub_cat_id", selectedSubcatId);
            Log.e(TAG, "ProductTask: Request >> " + jsonObject.toString());

            MyApiEndpointInterface apiService = ApiClient.getClient().create(MyApiEndpointInterface.class);
            Call<JsonObject> call = apiService.products(jsonObject);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.e(TAG, "ProductTask: Response >> " + response.body().toString());
                    String resp = response.body().toString();
                    productList.clear();
                    try {
                        JSONObject jsonObject = new JSONObject(resp);
                        if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("result");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                Gson gson = new Gson();
                                ProductData data = gson.fromJson(jsonArray.getJSONObject(i).toString(), ProductData.class);
                                productList.add(data);
                            }
                            publishProgress("200", "");
                        } else if (jsonObject.getString("status").equalsIgnoreCase("400")) {
                            String msg = jsonObject.getJSONArray("result").getJSONObject(0).getString("msg");
                            publishProgress("400", msg);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        publishProgress("400", getResources().getString(R.string.api_error_msg));
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    publishProgress("400", getResources().getString(R.string.api_error_msg));
                }
            });
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            hideProgressDialog();
            productAdapter.notifyDataSetChanged();
            String status = values[0];
            String msg = values[1];
            if (status.equalsIgnoreCase("400")) {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        }
    }

}
