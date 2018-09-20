package com.hvantage.medicineapp.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hvantage.medicineapp.R;
import com.hvantage.medicineapp.adapter.CartItemAdapter;
import com.hvantage.medicineapp.adapter.CartMedicineItemAdapter;
import com.hvantage.medicineapp.adapter.ConfirmOrderPrescAdapter;
import com.hvantage.medicineapp.database.DBHelper;
import com.hvantage.medicineapp.model.AddressData;
import com.hvantage.medicineapp.model.CartData;
import com.hvantage.medicineapp.model.PreMedicineData;
import com.hvantage.medicineapp.model.PrescriptionData;
import com.hvantage.medicineapp.model.ProductData;
import com.hvantage.medicineapp.retrofit.ApiClient;
import com.hvantage.medicineapp.retrofit.MyApiEndpointInterface;
import com.hvantage.medicineapp.util.AppConstants;
import com.hvantage.medicineapp.util.AppPreferences;
import com.hvantage.medicineapp.util.Functions;
import com.hvantage.medicineapp.util.ProgressBar;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfirmOrderActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ConfirmOrderActivity";
    //    ArrayList<PrescriptionModel> presList = new ArrayList<PrescriptionModel>();
    private Context context;
    private ProgressBar progressBar;
    private TextView tvTotalPrice, tvPayableAmt, tvDeliveryFee;
    private TextView tvCheckout;
    private LinearLayout llPrescription, llMedicine, llAmount;
    private LinearLayout llPayMode;
    private AddressData addressData;
    private TextView tvAddress1, tvAddress2, tvAddress3, tvAddress4;
    private TextView tvChangeAddress;
    private Double taxes = 0.0;
    private Double delivery_fee = 10.0;
    private String payment_mode = "Cash On Delivery";
    private RecyclerView recylcer_view, recylcer_view_items, recylcer_view_medicines;
    private ConfirmOrderPrescAdapter adapterPres;
    private CartItemAdapter adapterCart;
    private String selected_pres_id = "", selected_add_id = "";
    private EditText etNote;
    private RadioGroup rgOrderType;
    private String orderType = "1";
    private ArrayList<CartData> cartList = new ArrayList<CartData>();
    private ArrayList<PreMedicineData> medList = new ArrayList<PreMedicineData>();
    private double payable_amt = 0.0, subtotal_amt = 0.0;
    private ArrayList<PrescriptionData> prescList = new ArrayList<PrescriptionData>();
    private ArrayList<ProductData> list = new ArrayList<ProductData>();
    private AppCompatAutoCompleteTextView etSearch;
    private SearchBarAdapter adapter;
    private boolean prescription_required = false;
    private CartMedicineItemAdapter adapterMedicine;
    private AppCompatTextView tvAdd1;
    private boolean isExpand = false;
    private CoordinatorLayout coordinatorLayout;
    private AppCompatImageView imgArrow;
    private View bottomsheet_bottom;
    private BottomSheetBehavior<View> behavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_item);
        context = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        LocalBroadcastManager.getInstance(context).registerReceiver(new CartUpdateReciever(), new IntentFilter("cart_update"));

        selected_pres_id = AppPreferences.getSelectedPresId(context);
        selected_add_id = AppPreferences.getSelectedAddId(context);
        Log.e(TAG, "onCreate: selected_pres_id >> " + selected_pres_id);
        Log.e(TAG, "onCreate: selected_add_id >> " + selected_add_id);
        if (!AppPreferences.getUserId(context).equalsIgnoreCase("")) {
            addressData = new Gson().fromJson(AppPreferences.getSelectedAdd(context), AddressData.class);
            Log.e(TAG, "onCreate: addressData >> " + addressData);
            list = new DBHelper(context).getMedicines();
            Log.e(TAG, "onCreate: list >> " + list);
            init();
            setBottomBar();

            cartList = new DBHelper(context).getCartData();
            if (cartList != null) {
                if (cartList.size() > 0) {
                    Log.e(TAG, "onCreate: cartList >> " + cartList);
                    llMedicine.setVisibility(View.VISIBLE);
                    setRecyclerViewCart();
                    for (int i = 0; i < cartList.size(); i++) {
                        subtotal_amt = Functions.roundTwoDecimals(subtotal_amt + cartList.get(i).getItem_total_price());
                    }
                    payable_amt = subtotal_amt + delivery_fee;
                    tvTotalPrice.setText("Rs. " + subtotal_amt + "");
                    tvDeliveryFee.setText("Rs. " + delivery_fee + "");
                    tvPayableAmt.setText("Rs. " + payable_amt + "");
                } else {
                    llMedicine.setVisibility(View.GONE);
                    llAmount.setVisibility(View.GONE);
                    llPayMode.setVisibility(View.GONE);
                }
            } else {
                llMedicine.setVisibility(View.GONE);
                llAmount.setVisibility(View.GONE);
                llPayMode.setVisibility(View.GONE);
            }

           /* if (AppPreferences.getOrderType(context) == AppConstants.ORDER_TYPE.ORDER_WITH_PRESCRIPTION) {
                llPayMode.setVisibility(View.GONE);
            } else {
                llPayMode.setVisibility(View.VISIBLE);
            }*/

            if (addressData != null) {
                tvAddress1.setText(addressData.getName() + ", +91" + addressData.getContactNo());
                tvAddress2.setText(addressData.getAddress() + ", " + addressData.getLandmark());
                tvAddress3.setText(addressData.getCity() + ", " + addressData.getPincode());
                tvAddress4.setText(addressData.getState() + ", India");
            } else {
                startActivity(new Intent(context, SelectAddressActivity.class));
                finish();
            }
        } else {
            Toast.makeText(context, "Please Login", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(context, SignupActivity.class));
        }
    }

    private void init() {
        tvAdd1 = findViewById(R.id.tvAdd1);
        recylcer_view_items = (RecyclerView) findViewById(R.id.recylcer_view_items);
        recylcer_view = (RecyclerView) findViewById(R.id.recylcer_view);
        recylcer_view_medicines = (RecyclerView) findViewById(R.id.recylcer_view_medicines);
        etSearch = (AppCompatAutoCompleteTextView) findViewById(R.id.etSearch);
        llPrescription = (LinearLayout) findViewById(R.id.llPrescription);
        etNote = (EditText) findViewById(R.id.etNote);
        tvTotalPrice = (TextView) findViewById(R.id.tvTotalPrice);
        tvPayableAmt = (TextView) findViewById(R.id.tvPayableAmt);
        tvDeliveryFee = (TextView) findViewById(R.id.tvDeliveryFee);
        tvCheckout = (TextView) findViewById(R.id.tvCheckout);
        tvAddress1 = (TextView) findViewById(R.id.tvAddress1);
        tvAddress2 = (TextView) findViewById(R.id.tvAddress2);
        tvAddress3 = (TextView) findViewById(R.id.tvAddress3);
        tvAddress4 = (TextView) findViewById(R.id.tvAddress4);
        rgOrderType = findViewById(R.id.rgOrderType);
        tvChangeAddress = (TextView) findViewById(R.id.tvChangeAddress);
        llMedicine = (LinearLayout) findViewById(R.id.llMedicine);
        llAmount = (LinearLayout) findViewById(R.id.llAmount);
        llPayMode = (LinearLayout) findViewById(R.id.llPayMode);
        tvCheckout.setOnClickListener(this);
        tvAdd1.setOnClickListener(this);
        tvChangeAddress.setOnClickListener(this);
        rgOrderType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (rgOrderType.getCheckedRadioButtonId() == R.id.rbDelivery)
                    orderType = "1";
                else if (rgOrderType.getCheckedRadioButtonId() == R.id.rbTakeAway)
                    orderType = "2";
            }
        });
        if (list != null) {
            Log.e(TAG, "onCreateView: list >> " + list.size());
            etSearch.setThreshold(1);
            adapter = new SearchBarAdapter(context, R.layout.auto_complete_text, list);
            etSearch.setAdapter(adapter);
        }

        ((NestedScrollView) findViewById(R.id.touch_outside)).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Functions.hideSoftKeyboard(context, view);
                return false;
            }
        });

        recylcer_view_items.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Functions.hideSoftKeyboard(context, view);
                return false;
            }
        });

        recylcer_view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Functions.hideSoftKeyboard(context, view);
                return false;
            }
        });
    }

    private void setRecyclerView() {
        adapterPres = new ConfirmOrderPrescAdapter(context, prescList, null);
        recylcer_view.setLayoutManager(new LinearLayoutManager(context));
        recylcer_view.setAdapter(adapterPres);
        adapterPres.notifyDataSetChanged();
    }

    private void setRecyclerViewCart() {
        adapterCart = new CartItemAdapter(context, cartList);
        recylcer_view_items.setLayoutManager(new LinearLayoutManager(context));
        recylcer_view_items.setAdapter(adapterCart);
        adapterCart.notifyDataSetChanged();
    }

    private void setRecyclerViewMedicine() {
        adapterMedicine = new CartMedicineItemAdapter(context, medList);
        recylcer_view_medicines.setLayoutManager(new LinearLayoutManager(context));
        recylcer_view_medicines.setAdapter(adapterMedicine);
        adapterMedicine.notifyDataSetChanged();
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
        else if (item.getItemId() == R.id.action_cart) {
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvCheckout:
                Log.e(TAG, "onClick: orderType >> " + orderType);
                if (Functions.isConnectingToInternet(context)) {
                    if (prescription_required == true && CartActivity.selectedPresc == null) {
                        showUploadRxDialog();
                    } else if (CartActivity.selectedPresc == null) {
                        new OrderTaskWithout().execute();
                    } else {
                        new OrderTask().execute();
                    }
                } else {
                    Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.tvChangeAddress:
                startActivity(new Intent(context, SelectAddressActivity.class));
                break;
            case R.id.tvAdd1:
                if (isExpand)
                    behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                else
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                break;
            case R.id.imgArrow:
                behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                break;
        }
    }

    private void setBottomBar() {
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_content);
        bottomsheet_bottom = coordinatorLayout.findViewById(R.id.bottom_sheet);
        imgArrow = findViewById(R.id.imgArrow);

        imgArrow.setOnClickListener(this);
        behavior = BottomSheetBehavior.from(bottomsheet_bottom);
        behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomsheet_bottom, int newState) {
                Log.e("onStateChanged", "onStateChanged:" + newState);
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        Log.d(TAG, "onStateChanged: STATE_HIDDEN");
                        Functions.hideSoftKeyboard(context, bottomsheet_bottom);
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        isExpand = true;
                        Log.d(TAG, "onStateChanged: STATE_EXPANDED");
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        isExpand = false;
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        Log.d(TAG, "onStateChanged: STATE_DRAGGING");
                        Functions.hideSoftKeyboard(context, bottomsheet_bottom);
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        Log.d(TAG, "onStateChanged: STATE_SETTLING");
                        Functions.hideSoftKeyboard(context, bottomsheet_bottom);
                        break;

                }
            }

            @Override
            public void onSlide(@NonNull View bottomsheet_bottom, float slideOffset) {
                animateBottomSheetArrows(slideOffset);
            }
        });
    }

    private void animateBottomSheetArrows(float slideOffset) {
        imgArrow.setRotation(slideOffset * -180);
    }

    private void showUploadRxDialog() {
        new AlertDialog.Builder(context)
                .setTitle("Upload Your Prescription")
                .setMessage("One or more items in your cart requires a valid prescription with doctor's name and drug detail clearly visible.")
                .setPositiveButton("Upload", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(context, AddPrescActivity.class));
                    }
                })
                .setNegativeButton("Choose Existing", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(context, SelectPrescActivity.class));
                    }
                })
                .show();
    }

    private void dialogOrderSent() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setCancelable(false);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_order_sent, null);
        AppCompatButton btnSubmit = dialogView.findViewById(R.id.btnSubmit);
        dialog.setView(dialogView);
        final AlertDialog alertDialog = dialog.create();
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, MainActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });
        alertDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (CartActivity.selectedPresc != null) {
            prescList.add(CartActivity.selectedPresc);
            Log.e(TAG, "onCreate: prescList >> " + prescList);
            setRecyclerView();
            llPrescription.setVisibility(View.VISIBLE);
            llAmount.setVisibility(View.GONE);
            llPayMode.setVisibility(View.GONE);
            medList = CartActivity.selectedPresc.getMedicineDetails();
            Log.e(TAG, "onResume: medList >> " + medList);
            if (CartActivity.selectedPresc.getMedicineDetails() != null) {
                setRecyclerViewMedicine();
            } else {
            }
        } else {
            llPrescription.setVisibility(View.GONE);
            llAmount.setVisibility(View.VISIBLE);
            llPayMode.setVisibility(View.VISIBLE);
        }
    }

    class CartUpdateReciever extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            cartList = new DBHelper(context).getCartData();
            Log.e(TAG, "onReceive: cartList >> " + cartList);

            if (cartList != null) {
                cartList.clear();
                payable_amt = 0;
                subtotal_amt = 0;
                if (cartList.size() > 0) {
                    Log.e(TAG, "onCreate: cartList >> " + cartList);
                    llMedicine.setVisibility(View.VISIBLE);
                    setRecyclerViewCart();
                    for (int i = 0; i < cartList.size(); i++) {
                        subtotal_amt = Functions.roundTwoDecimals(subtotal_amt + cartList.get(i).getItem_total_price());
                        if (prescription_required == false)
                            if (cartList.get(i).isIs_prescription_required() == true) {
                                prescription_required = true;
                            }
                    }
                    payable_amt = subtotal_amt + delivery_fee;
                    tvTotalPrice.setText("Rs. " + subtotal_amt + "");
                    tvDeliveryFee.setText("Rs. " + delivery_fee + "");
                    tvPayableAmt.setText("Rs. " + payable_amt + "");
                } else {
                    llMedicine.setVisibility(View.GONE);
                    llAmount.setVisibility(View.GONE);
                    llPayMode.setVisibility(View.GONE);
                }
            } else {
                llMedicine.setVisibility(View.GONE);
                llAmount.setVisibility(View.GONE);
                llPayMode.setVisibility(View.GONE);
            }

            Log.e(TAG, "onReceive: CartActivity.selectedPresc  >> " + CartActivity.selectedPresc);
            Log.e(TAG, "onReceive: AppPreferences.getSelectedPresId(context) >> " + AppPreferences.getSelectedPresId(context));
            if (CartActivity.selectedPresc == null && AppPreferences.getSelectedPresId(context).equalsIgnoreCase("")) {
                llAmount.setVisibility(View.VISIBLE);
                llPayMode.setVisibility(View.GONE);
                llPrescription.setVisibility(View.GONE);
            }
        }
    }

    class OrderTask extends AsyncTask<Void, String, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            JsonObject jsonObject = new JsonObject();
//            jsonObject.addProperty("method", AppConstants.METHODS.PLACE_ORDER_WITH_PRESCRIPTION);
            jsonObject.addProperty("method", AppConstants.METHODS.PLACEORDERWITHPRESCRIPTION);
            jsonObject.addProperty("user_id", AppPreferences.getUserId(context));
            jsonObject.add("prescription_data", new Gson().toJsonTree(CartActivity.selectedPresc));
            jsonObject.addProperty("address_id", selected_add_id);
            jsonObject.addProperty("order_type", orderType);
            jsonObject.addProperty("payment_mode", "1");
            jsonObject.addProperty("payment_type", "cash");
            jsonObject.addProperty("note", etNote.getText().toString());
            if (cartList == null)
                jsonObject.add("additional_items", new JsonArray());
            else
                jsonObject.add("additional_items", new GsonBuilder().create().toJsonTree(cartList).getAsJsonArray());
            jsonObject.addProperty("payable_amount", 0);
            jsonObject.addProperty("cod_charges ", 0);
            jsonObject.addProperty("gst_tax_amt", 0);
            jsonObject.addProperty("other_tax_amt", 0);
            jsonObject.addProperty("total_amount", 0);
            jsonObject.addProperty("promo_code_applied", "");

            Log.e(TAG, "OrderTask: Request >> " + jsonObject.toString());

            MyApiEndpointInterface apiService = ApiClient.getClient().create(MyApiEndpointInterface.class);
            Call<JsonObject> call = apiService.order(jsonObject);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.e(TAG, "OrderTask: Response >> " + response.body().toString());
                    String resp = response.body().toString();
                    try {
                        JSONObject jsonObject = new JSONObject(resp);
                        if (jsonObject.getString("status").equalsIgnoreCase("200")) {
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
            String status = values[0];
            String msg = values[1];
            if (status.equalsIgnoreCase("200")) {
                dialogOrderSent();
//                startActivity(new Intent(context, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
            } else if (status.equalsIgnoreCase("400")) {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        }
    }

    class OrderTaskWithout extends AsyncTask<Void, String, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("method", AppConstants.METHODS.PLACE_ORDER_WITHOUT_PRESCRIPTION);
            jsonObject.addProperty("user_id", AppPreferences.getUserId(context));
            jsonObject.addProperty("prescription_id", "0");
            jsonObject.addProperty("address_id", selected_add_id);
            jsonObject.addProperty("order_type", selected_add_id);
            jsonObject.addProperty("payment_mode", orderType);
            jsonObject.addProperty("payment_type", "cash");
            jsonObject.addProperty("note", etNote.getText().toString());
            jsonObject.add("additional_items", new GsonBuilder().create().toJsonTree(cartList).getAsJsonArray());
            jsonObject.addProperty("payable_amount", payable_amt);
            jsonObject.addProperty("cod_charges ", delivery_fee);
            jsonObject.addProperty("gst_tax_amt", 0);
            jsonObject.addProperty("other_tax_amt", 0);
            jsonObject.addProperty("total_amount", subtotal_amt);
            jsonObject.addProperty("promo_code_applied", "");

            Log.e(TAG, "OrderTask: Request >> " + jsonObject.toString());

            MyApiEndpointInterface apiService = ApiClient.getClient().create(MyApiEndpointInterface.class);
            Call<JsonObject> call = apiService.order(jsonObject);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.e(TAG, "OrderTask: Response >> " + response.body().toString());
                    String resp = response.body().toString();
                    try {
                        JSONObject jsonObject = new JSONObject(resp);
                        if (jsonObject.getString("status").equalsIgnoreCase("200")) {
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
            String status = values[0];
            String msg = values[1];
            if (status.equalsIgnoreCase("200")) {
                dialogOrderSent();
                //startActivity(new Intent(context, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
            } else if (status.equalsIgnoreCase("400")) {
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
                        ProductData data = list.get(position);
                        Log.e(TAG, "onDataChange: data >> " + data);
                        startActivity(new Intent(context, ProductDetailActivity.class).putExtra("medicine_data", data));
                        etSearch.setText("");
                    }
                });
                btnAddToCart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!AppPreferences.getUserId(context).equalsIgnoreCase("")) {
                            double item_total = Integer.parseInt(tvQty.getText().toString()) * Double.parseDouble(list.get(position).getPriceDiscount());
                            CartData model = new CartData(
                                    list.get(position).getProductId(),
                                    list.get(position).getName(),
                                    list.get(position).getImage(),
                                    Integer.parseInt(tvQty.getText().toString()),
                                    Double.parseDouble(list.get(position).getPriceDiscount()),
                                    item_total,
                                    list.get(position).getPrescriptionRequired()
                            );
                            if (new DBHelper(context).addToCart(model)) {
                                etSearch.setText("");
                                Toast.makeText(context, "Added", Toast.LENGTH_SHORT).show();
                                MainActivity.setupBadge();
                                LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("cart_update"));
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
