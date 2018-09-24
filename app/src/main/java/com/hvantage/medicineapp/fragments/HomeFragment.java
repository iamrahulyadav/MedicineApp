package com.hvantage.medicineapp.fragments;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hvantage.medicineapp.R;
import com.hvantage.medicineapp.activity.CartActivity;
import com.hvantage.medicineapp.activity.MainActivity;
import com.hvantage.medicineapp.activity.ProductDetailActivity;
import com.hvantage.medicineapp.activity.SignupActivity;
import com.hvantage.medicineapp.adapter.CategoryAdapter;
import com.hvantage.medicineapp.adapter.DailyNeedProductAdapter;
import com.hvantage.medicineapp.database.DBHelper;
import com.hvantage.medicineapp.model.CartData;
import com.hvantage.medicineapp.model.CategoryData;
import com.hvantage.medicineapp.model.ProductData;
import com.hvantage.medicineapp.retrofit.ApiClient;
import com.hvantage.medicineapp.retrofit.MyApiEndpointInterface;
import com.hvantage.medicineapp.util.AppConstants;
import com.hvantage.medicineapp.util.AppPreferences;
import com.hvantage.medicineapp.util.FragmentIntraction;
import com.hvantage.medicineapp.util.Functions;
import com.hvantage.medicineapp.util.ProgressBar;
import com.hvantage.medicineapp.util.RecyclerItemClickListener;
import com.hvantage.medicineapp.util.SliderUtil;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;


public class HomeFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "HomeFragment";
    private static final int REQUEST_ALL_PERMISSIONS = 100, REQUEST_CALL_PERMISSION = 101, REQ_CODE_SPEECH_INPUT = 101;
    ArrayList<CategoryData> catList = new ArrayList<CategoryData>();
    ArrayList<ProductData> productList = new ArrayList<ProductData>();
    ArrayList<Bitmap> offerList = new ArrayList<Bitmap>();
    private RecyclerView recylcer_view, recylcer_view_daily;
    private CategoryAdapter categoryAdapter;
    private DailyNeedProductAdapter productAdapter;
    private Context context;
    private View rootView;
    private FragmentIntraction intraction;
    private CardView btnUpload;
    private ImageView btnVoiceInput;
    private AppCompatAutoCompleteTextView etSearch;
    private ArrayList<ProductData> searchList;
    private ProgressBar progressBar;
    private FloatingActionMenu floatingActionMenu;
    private ViewPager viewPagerOffers;
    private SearchBarAdapter searchAdapter;

    private void setFloatingButton() {
        new FloatingButton().showFloatingButton(rootView, context);
        new FloatingButton().setFloatingButtonControls(rootView);
    }

    private void createCustomAnimation() {
        AnimatorSet set = new AnimatorSet();
        ObjectAnimator scaleOutX = ObjectAnimator.ofFloat(floatingActionMenu.getMenuIconView(), "scaleX", 1.0f, 0.2f);
        ObjectAnimator scaleOutY = ObjectAnimator.ofFloat(floatingActionMenu.getMenuIconView(), "scaleY", 1.0f, 0.2f);
        ObjectAnimator scaleInX = ObjectAnimator.ofFloat(floatingActionMenu.getMenuIconView(), "scaleX", 0.2f, 1.0f);
        ObjectAnimator scaleInY = ObjectAnimator.ofFloat(floatingActionMenu.getMenuIconView(), "scaleY", 0.2f, 1.0f);

        scaleOutX.setDuration(50);
        scaleOutY.setDuration(50);

        scaleInX.setDuration(150);
        scaleInY.setDuration(150);

        scaleInX.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                floatingActionMenu.getMenuIconView().setImageResource(floatingActionMenu.isOpened()
                        ? R.drawable.fab_back : R.drawable.fab_plus);
            }
        });

        set.play(scaleOutX).with(scaleOutY);
        set.play(scaleInX).with(scaleInY).after(scaleOutX);
        set.setInterpolator(new OvershootInterpolator(2));
        floatingActionMenu.setIconToggleAnimatorSet(set);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = container.getContext();
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        if (intraction != null) {
            intraction.actionbarsetTitle(getResources().getString(R.string.app_name));
        }
        init();
        setFloatingButton();
//        searchList = new DBHelper(context).getMedicines();
        if (Functions.isConnectingToInternet(context)) {
            new CategoryTask().execute();
            new ProductTask().execute();
        } else {
            Toast.makeText(context, getResources().getString(R.string.no_internet_text), Toast.LENGTH_SHORT).show();
        }
        searchList = new ArrayList<>();
        etSearch.setThreshold(4);
        searchAdapter = new SearchBarAdapter(context, R.layout.auto_complete_text, searchList);
        etSearch.setAdapter(searchAdapter);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 3) {
                    new SearchData().execute();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        AppPreferences.setSelectedPresId(context, "");
        CartActivity.selectedPresc = null;
        return rootView;
    }

    private void setProductAdapter() {
        recylcer_view_daily = (RecyclerView) rootView.findViewById(R.id.recylcer_view_daily);
        productAdapter = new DailyNeedProductAdapter(context, productList);
        recylcer_view_daily.setLayoutManager(new LinearLayoutManager(context));
        recylcer_view_daily.setAdapter(productAdapter);
        productAdapter.notifyDataSetChanged();
    }

    private void setSlider() {
        viewPagerOffers = (ViewPager) rootView.findViewById(R.id.viewPagerOffers);
        TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPagerOffers, true);
        SliderUtil.setSlider(context, viewPagerOffers);
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

    private void init() {
        etSearch = (AppCompatAutoCompleteTextView) rootView.findViewById(R.id.etSearch);
        btnUpload = (CardView) rootView.findViewById(R.id.btnUpload);
        btnVoiceInput = (ImageView) rootView.findViewById(R.id.btnVoiceInput);
        btnUpload.setOnClickListener(this);
        btnVoiceInput.setOnClickListener(this);
        ((NestedScrollView) rootView.findViewById(R.id.container)).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Functions.hideSoftKeyboard(context, view);
                return false;
            }
        });
        ((CardView) rootView.findViewById(R.id.fabCall)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkCallPermission())
                    Functions.callOrder(context);
                else {
                    /*Toast.makeText(context, "Call Permission Denied!", Toast.LENGTH_SHORT).show();*/
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{
                                    Manifest.permission.CALL_PHONE
                            },
                            REQUEST_CALL_PERMISSION);
                }
            }
        });
        setSlider();
        setCategoryAdapter();
        setProductAdapter();
    }

    private void setCategoryAdapter() {
        recylcer_view = (RecyclerView) rootView.findViewById(R.id.recylcer_view);
        categoryAdapter = new CategoryAdapter(context, catList);
        recylcer_view.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        recylcer_view.setAdapter(categoryAdapter);
        recylcer_view.addOnItemTouchListener(new RecyclerItemClickListener(context, recylcer_view, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (catList.get(position).getCatName().contains("Prescription")) {
                    PrescriptionCatFragment fragment = new PrescriptionCatFragment();
                    Bundle args = new Bundle();
                    args.putParcelable("data", catList.get(position));
                    fragment.setArguments(args);
                    FragmentManager manager = getFragmentManager();
                    FragmentTransaction ft = manager.beginTransaction();
                    ft.replace(R.id.main_container, fragment);
                    ft.addToBackStack(null);
                    ft.commitAllowingStateLoss();
                } else {
                    BrowseCategoryFragment fragment = new BrowseCategoryFragment();
                    Bundle args = new Bundle();
                    args.putParcelable("data", catList.get(position));
                    fragment.setArguments(args);
                    FragmentManager manager = getFragmentManager();
                    FragmentTransaction ft = manager.beginTransaction();
                    ft.replace(R.id.main_container, fragment);
                    ft.addToBackStack(null);
                    ft.commitAllowingStateLoss();
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));
        categoryAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentIntraction) {
            intraction = (FragmentIntraction) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        intraction = null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnUpload:
                if (!AppPreferences.getUserId(context).equalsIgnoreCase("")) {
                    FragmentManager manager = getFragmentManager();
                    FragmentTransaction ft = manager.beginTransaction();
                    ft.replace(R.id.main_container, new UploadPrecriptionFragment());
                    ft.addToBackStack(null);
                    ft.commitAllowingStateLoss();
                } else {
                    startActivity(new Intent(context, SignupActivity.class));
                }
                break;
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
            Toast.makeText(getActivity(), getString(R.string.speech_not_supported), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkPermission() {
        if ((ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                && (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                && (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {
            if ((ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE))
                    && (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE))) {
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA,
                        },
                        REQUEST_ALL_PERMISSIONS);
            }
            return false;
        } else {
            return true;
        }
    }

    private boolean checkCallPermission() {
        if ((ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)) {
            if ((ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CALL_PHONE))) {
                Functions.callOrder(context);
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{
                                Manifest.permission.CALL_PHONE
                        },
                        REQUEST_CALL_PERMISSION);
            }
            return false;
        } else {
            return true;
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
                            searchAdapter.notifyDataSetChanged();

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

    public class FloatingButton {
        FrameLayout bckgroundDimmer;
        FloatingActionButton button1, button2;

        public void showFloatingButton(final View activity, final Context mContext) {

            floatingActionMenu = (FloatingActionMenu) activity.findViewById(R.id.material_design_android_floating_action_menu);
            button1 = (FloatingActionButton) activity.findViewById(R.id.material_design_floating_action_menu_item1);
            button2 = (FloatingActionButton) activity.findViewById(R.id.material_design_floating_action_menu_item2);
            createCustomAnimation();

            button1.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Fragment fragment = new MyPrescriptionFragment();
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                    fragmentTransaction.replace(R.id.main_container, fragment, fragment.getTag());
                    fragmentTransaction.commitAllowingStateLoss();
                    fragmentTransaction.addToBackStack(null);
                    floatingActionMenu.close(true);
                }
            });
            button2.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Fragment fragment = new UploadPrecriptionFragment();
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                    fragmentTransaction.replace(R.id.main_container, fragment, fragment.getTag());
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commitAllowingStateLoss();
                    floatingActionMenu.close(true);
                }
            });
        }

        public void setFloatingButtonControls(View activity) {
            bckgroundDimmer = (FrameLayout) activity.findViewById(R.id.background_dimmer);
            floatingActionMenu = (FloatingActionMenu) activity.findViewById(R.id.material_design_android_floating_action_menu);
            floatingActionMenu.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
                @Override
                public void onMenuToggle(boolean opened) {
                    if (opened) {
                        bckgroundDimmer.setVisibility(View.VISIBLE);
                    } else {
                        bckgroundDimmer.setVisibility(View.GONE);
                    }
                }
            });
            bckgroundDimmer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (floatingActionMenu.isOpened()) {
                        floatingActionMenu.close(true);
                        bckgroundDimmer.setVisibility(View.GONE);
                        //menu opened
                    }
                }
            });
        }
    }

    class ProductTask extends AsyncTask<Void, String, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            showProgressDialog();
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
                        publishProgress("400", getActivity().getResources().getString(R.string.api_error_msg));
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
//            hideProgressDialog();
            productAdapter.notifyDataSetChanged();
            String status = values[0];
            String msg = values[1];
            if (status.equalsIgnoreCase("400")) {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
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
                        publishProgress("400", getActivity().getResources().getString(R.string.api_error_msg));
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
            categoryAdapter.notifyDataSetChanged();
            String status = values[0];
            String msg = values[1];
            if (status.equalsIgnoreCase("400")) {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class SearchBarAdapter extends ArrayAdapter<ProductData> {

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
                CardView btnAddToCart = (CardView) view.findViewById(R.id.btnAddToCart);
                if (tvName != null) {
                    tvName.setText(people.getName());
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

}
