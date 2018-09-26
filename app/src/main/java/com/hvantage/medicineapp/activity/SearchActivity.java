package com.hvantage.medicineapp.activity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hvantage.medicineapp.R;
import com.hvantage.medicineapp.adapter.SearchAdapter;
import com.hvantage.medicineapp.database.DBHelper;
import com.hvantage.medicineapp.model.CartData;
import com.hvantage.medicineapp.model.ProductData;
import com.hvantage.medicineapp.retrofit.ApiClient;
import com.hvantage.medicineapp.retrofit.MyApiEndpointInterface;
import com.hvantage.medicineapp.util.AppConstants;
import com.hvantage.medicineapp.util.AppPreferences;
import com.hvantage.medicineapp.util.Functions;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int REQ_CODE_SPEECH_INPUT = 101;
    private AppCompatEditText etSearch;
    private ArrayList<ProductData> searchList;
    private Context context;
    private String TAG = "SearchActivity";
    private RecyclerView recylcer_view_daily;
    private SearchAdapter productAdapter;
    private ImageView btnVoiceInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        context = this;
        init();
    }

    private void init() {
        searchList = new ArrayList<ProductData>();
        etSearch = findViewById(R.id.etSearch);
        recylcer_view_daily = (RecyclerView) findViewById(R.id.recylcer_view_daily);
        btnVoiceInput = (ImageView) findViewById(R.id.btnVoiceInput);
        btnVoiceInput.setOnClickListener(this);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 3) {
                    new SearchData().execute();
                } else {
                    searchList.clear();
                    productAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ((ConstraintLayout) findViewById(R.id.container)).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                finish();
                return false;
            }
        });

        setProductAdapter();
    }

    private void setProductAdapter() {
        productAdapter = new SearchAdapter(context, searchList);
        recylcer_view_daily.setLayoutManager(new LinearLayoutManager(context));
        recylcer_view_daily.setAdapter(productAdapter);
        productAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnVoiceInput:
                promptSpeechInput();
                break;
        }
    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak Now");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(context, getString(R.string.speech_not_supported), Toast.LENGTH_SHORT).show();
        }
    }

    class SearchData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("method", AppConstants.METHODS.SEARCH_PRODUCT);
            jsonObject.addProperty("key", etSearch.getText().toString());
            Log.e(TAG, "SearchData: Request >> " + jsonObject.toString());

            MyApiEndpointInterface apiService = ApiClient.getClient().create(MyApiEndpointInterface.class);
            Call<JsonObject> call = apiService.products(jsonObject);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.e(TAG, "SearchData: Response >> " + response.body().toString());
                    String resp = response.body().toString();
                    try {
                        JSONObject jsonObject = new JSONObject(resp);
                        if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                            searchList.clear();
                            JSONArray jsonArray = jsonObject.getJSONArray("result");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                Gson gson = new Gson();
                                ProductData data = gson.fromJson(jsonArray.getJSONObject(i).toString(), ProductData.class);
                                searchList.add(data);
                            }
                            productAdapter.notifyDataSetChanged();

                        } else if (jsonObject.getString("status").equalsIgnoreCase("400")) {
                            String msg = jsonObject.getJSONArray("result").getJSONObject(0).getString("msg");
                            Log.e(TAG, "onResponse: " + msg);
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "onFailure: " + e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Log.e(TAG, "onFailure: " + t.getMessage());
                }
            });
            return null;
        }
    }


    private class SearchBarAdapter extends ArrayAdapter<ProductData> {

        Context context;
        int resource;
        ArrayList<ProductData> items, tempItems, suggestions;
        /**
         * Custom Filter implementation for custom suggestions we provide.
         */
        Filter nameFilter = new Filter() {
            @Override
            public CharSequence convertResultToString(Object resultValue) {
                String str = ((ProductData) resultValue).getName();
                return str;
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                if (constraint != null) {
                    suggestions.clear();
                    for (ProductData people : tempItems) {
                        if (people.getName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                            suggestions.add(people);
                        }
                    }
                    FilterResults filterResults = new FilterResults();
                    filterResults.values = suggestions;
                    filterResults.count = suggestions.size();
                    return filterResults;
                } else {
                    return new FilterResults();
                }
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                ArrayList<ProductData> filterList = (ArrayList<ProductData>) results.values;
                if (results != null && results.count > 0) {
                    clear();
                    for (ProductData people : filterList) {
                        add(people);
                        notifyDataSetChanged();
                    }
                }
            }
        };

        public SearchBarAdapter(Context context, int resource, ArrayList<ProductData> items) {
            super(context, resource, items);
            this.context = context;
            this.resource = resource;
            this.items = items;
            tempItems = new ArrayList<ProductData>(items); // this makes the difference.
            suggestions = new ArrayList<ProductData>();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.auto_complete_text, parent, false);
            }
            ProductData people = items.get(position);
            if (people != null) {
                TextView tvName = (TextView) view.findViewById(R.id.tvName);
                TextView tvPrice = (TextView) view.findViewById(R.id.tvPrice);
                TextView tvPriceDrop = (TextView) view.findViewById(R.id.tvPriceDrop);
                TextView tvPlus = (TextView) view.findViewById(R.id.tvPlus);
                TextView tvMinus = (TextView) view.findViewById(R.id.tvMinus);
                final TextView tvQty = (TextView) view.findViewById(R.id.tvQty);
                ImageView imgThumb = (ImageView) view.findViewById(R.id.imgThumb);
                AppCompatButton btnAddToCart = (AppCompatButton) view.findViewById(R.id.btnAddToCart);
                if (tvName != null) {
                    tvName.setText(people.getName());
                    people.setPriceDiscount(people.getPriceDiscount().replace(",", ""));
                    people.setPriceMrp(people.getPriceMrp().replace(",", ""));
                    tvPrice.setText("Rs." + Functions.roundTwoDecimals(Double.parseDouble(people.getPriceDiscount())));
                    tvPriceDrop.setText("Rs." + Functions.roundTwoDecimals(Double.parseDouble(people.getPriceMrp())));
                    tvPriceDrop.setPaintFlags(tvPriceDrop.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                    if (!people.getImage().equalsIgnoreCase("")) {
                        Picasso.with(context)
                                .load(people.getImage())
                                .placeholder(R.drawable.no_image_placeholder)
                                .resize(60, 60)
                                .into(imgThumb);
                    }
                }

                tvMinus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int qty = Integer.parseInt(tvQty.getText().toString());
                        if (qty > 1)
                            qty--;
                        tvQty.setText(String.valueOf(qty));
                    }
                });

                tvPlus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int qty = Integer.parseInt(tvQty.getText().toString());
                        if (qty < 10)
                            qty++;
                        tvQty.setText(String.valueOf(qty));
                    }
                });

                tvName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ProductData data = searchList.get(position);
                        Log.e(TAG, "onDataChange: data >> " + data);
                        startActivity(new Intent(context, ProductDetailActivity.class).putExtra("medicine_data", data));
                        etSearch.setText("");
                    }
                });
                btnAddToCart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!AppPreferences.getUserId(context).equalsIgnoreCase("")) {
                            double item_total = Integer.parseInt(tvQty.getText().toString()) * Double.parseDouble(searchList.get(position).getPriceDiscount());
                            CartData model = new CartData(
                                    searchList.get(position).getProductId(),
                                    searchList.get(position).getName(),
                                    searchList.get(position).getImage(),
                                    Integer.parseInt(tvQty.getText().toString()),
                                    Double.parseDouble(searchList.get(position).getPriceDiscount()),
                                    item_total,
                                    searchList.get(position).getPrescriptionRequired()
                            );
                            if (new DBHelper(context).addToCart(model)) {
                                etSearch.setText("");
                                Toast.makeText(context, "Added", Toast.LENGTH_SHORT).show();
                                MainActivity.setupBadge();
                            } else
                                Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Please Login", Toast.LENGTH_SHORT).show();
                            context.startActivity(new Intent(context, SignupActivity.class));
                        }
                    }
                });
            }
            return view;
        }

        @Override
        public Filter getFilter() {
            return nameFilter;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    etSearch.setText(result.get(0));
                }
                break;
            }
        }
    }


}
