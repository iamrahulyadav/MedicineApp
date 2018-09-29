package com.hvantage.medicineapp.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.JsonObject;
import com.hvantage.medicineapp.R;
import com.hvantage.medicineapp.database.DBHelper;
import com.hvantage.medicineapp.fragments.AddPrescrFragment;
import com.hvantage.medicineapp.fragments.BrowseCatFragment;
import com.hvantage.medicineapp.fragments.HomeFragment;
import com.hvantage.medicineapp.fragments.MyAccountFragment;
import com.hvantage.medicineapp.fragments.MyOrderFragment;
import com.hvantage.medicineapp.fragments.MyPrescriptionFragment;
import com.hvantage.medicineapp.fragments.UploadPrecriptionFragment;
import com.hvantage.medicineapp.fragments.VaultFragment;
import com.hvantage.medicineapp.model.CartData;
import com.hvantage.medicineapp.retrofit.ApiClient;
import com.hvantage.medicineapp.retrofit.MyApiEndpointInterface;
import com.hvantage.medicineapp.util.AppConstants;
import com.hvantage.medicineapp.util.AppPreferences;
import com.hvantage.medicineapp.util.FragmentIntraction;
import com.hvantage.medicineapp.util.Functions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, FragmentIntraction {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_ALL_PERMISSIONS = 100;
    private static Context context;
    private static TextView textCartItemCount;
    public static MenuItem menuSearch = null;
    private static int mCartItemCount = 0;
    private TextView toolbar_title;
    private NavigationView navigationView;
    private TextView tvLogin, tvUsername;
    private String mToken;
    private FloatingActionMenu floatingActionMenu;

    public static void setupBadge() {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_main);
        context = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar_title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        initDrawer(toolbar);
        setDefaultFragment();
        Log.e(TAG, "onCreate: fcm token >> " + FirebaseInstanceId.getInstance().getToken());
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(MainActivity.this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                mToken = instanceIdResult.getToken();
                Log.e(TAG, "onSuccess: Token >> " + mToken);
                if (Functions.isConnectingToInternet(context))
                    new UpdateFCMTask().execute();
            }
        });
        if (AppPreferences.getUserId(context).equalsIgnoreCase("")) {
            hideMenus();
            tvUsername.setVisibility(View.GONE);
            tvLogin.setVisibility(View.VISIBLE);
        } else {
            tvUsername.setVisibility(View.VISIBLE);
            tvLogin.setVisibility(View.GONE);
            Log.e(TAG, "onCreate: uid >> " + AppPreferences.getUserId(context));
            Log.e(TAG, "onCreate: name >> " + AppPreferences.getUserName(context));
            tvUsername.setText("Hello, " + AppPreferences.getUserName(context));
        }
        setFloatingButton();
        // checkPermission();
    }

    private boolean checkPermission() {
        if ((ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                && (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                && (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {
            if ((ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                    && (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE))) {
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_ALL_PERMISSIONS: {
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults.length > 0 && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        //permission granted
                    } else {
                        Toast.makeText(MainActivity.this, "Permission Denied.", Toast.LENGTH_LONG).show();
                    }
                }
            }
            break;
        }
    }

    private void hideMenus() {
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.nav_upload_pre).setVisible(false);
        nav_Menu.findItem(R.id.nav_my_pre).setVisible(false);
        nav_Menu.findItem(R.id.nav_vault).setVisible(false);
        nav_Menu.findItem(R.id.nav_orders).setVisible(false);
        nav_Menu.findItem(R.id.nav_myaccount).setVisible(false);
        nav_Menu.findItem(R.id.nav_logout).setVisible(false);
    }

    private void setDefaultFragment() {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.replace(R.id.main_container, new HomeFragment());
        ft.commitAllowingStateLoss();
    }

    private void initDrawer(Toolbar toolbar) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        tvLogin = (TextView) navigationView.getHeaderView(0).findViewById(R.id.tvLogin);
        tvUsername = (TextView) navigationView.getHeaderView(0).findViewById(R.id.tvUsername);
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, SignupActivity.class));
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
            }
        });

        ImageView imgFacebook = (ImageView) navigationView.findViewById(R.id.imgFacebook);
        imgFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.facebook_page_url))));
            }
        });
        ImageView imgInstagram = (ImageView) navigationView.findViewById(R.id.imgInstagram);
        imgInstagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.instagram_page_url))));
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        final MenuItem menuItem = menu.findItem(R.id.action_cart);
        menuSearch = menu.findItem(R.id.action_search);
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

    @Override
    protected void onResume() {
        super.onResume();
        setupBadge();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_cart:
                if (!AppPreferences.getUserId(context).equalsIgnoreCase("")) {
                    startActivity(new Intent(context, CartActivity.class));
                } else {
                    startActivity(new Intent(MainActivity.this, SignupActivity.class));
                }
                break;
            case R.id.action_search:
                startActivity(new Intent(context, SearchActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        Fragment fragment = null;
        switch (id) {
            case R.id.nav_home:
                fragment = new HomeFragment();
                ft.replace(R.id.main_container, fragment);
                ft.commitAllowingStateLoss();
                clearBackStack();
                break;
            case R.id.nav_category:
                fragment = new BrowseCatFragment();
                ft.replace(R.id.main_container, fragment);
                ft.addToBackStack(null);
                ft.commitAllowingStateLoss();
                break;
            case R.id.nav_my_pre:
                fragment = new MyPrescriptionFragment();
                ft.replace(R.id.main_container, fragment);
                ft.addToBackStack(null);
                ft.commitAllowingStateLoss();
                break;
            case R.id.nav_upload_pre:
                if (!AppPreferences.getUserId(context).equalsIgnoreCase("")) {
                    fragment = new UploadPrecriptionFragment();
                    ft.replace(R.id.main_container, fragment);
                    ft.addToBackStack(null);
                    ft.commitAllowingStateLoss();
                } else {
                    startActivity(new Intent(MainActivity.this, SignupActivity.class));
                }

                break;
            case R.id.nav_vault:
                fragment = new VaultFragment();
                ft.replace(R.id.main_container, fragment);
                ft.addToBackStack(null);
                ft.commitAllowingStateLoss();
                break;
            case R.id.nav_orders:
                fragment = new MyOrderFragment();
                ft.replace(R.id.main_container, fragment);
                ft.addToBackStack(null);
                ft.commitAllowingStateLoss();
                break;
            case R.id.nav_myaccount:
                fragment = new MyAccountFragment();
                ft.replace(R.id.main_container, fragment);
                ft.addToBackStack(null);
                ft.commitAllowingStateLoss();
                break;
            case R.id.nav_discount_offer:
               /* fragment = new OfferDiscountFragment();
                ft.replace(R.id.main_container, fragment);
                ft.addToBackStack(null);
                ft.commitAllowingStateLoss();*/
                startActivity(new Intent(context, OffersActivity.class));
                break;
            case R.id.nav_logout:
                logoutAlert();
                break;
            case R.id.nav_cust_support:
                dialogCustomerSupport();
                break;
            case R.id.nav_share:
                Intent intent2 = new Intent();
                intent2.setAction(Intent.ACTION_SEND);
                intent2.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.download_app_text));
                intent2.setType("text/plain");
                startActivity(Intent.createChooser(intent2, "Share via"));
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void dialogCustomerSupport() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setCancelable(true);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_order_support, null);
        dialog.setView(dialogView);
        final AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }

    private void clearBackStack() {
        FragmentManager manager = getSupportFragmentManager();
        if (manager.getBackStackEntryCount() > 0) {
            FragmentManager.BackStackEntry first = manager.getBackStackEntryAt(0);
            manager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    private void logoutAlert() {
        new AlertDialog.Builder(context)
                .setMessage("Do you want to logout?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AppPreferences.clearPreference(context);
                        startActivity(new Intent(context, SignupActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .show();
    }

    @Override
    public void actionbarsetTitle(String title) {
        toolbar_title.setText(title);
    }

    class UpdateFCMTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("method", AppConstants.METHODS.UPDATE_FCM_TOKEN);
            jsonObject.addProperty("user_id", AppPreferences.getUserId(context));
            jsonObject.addProperty("fcm_token", mToken);
            Log.e(TAG, "UpdateFCMTask: Request >> " + jsonObject.toString());

            MyApiEndpointInterface apiService = ApiClient.getClient().create(MyApiEndpointInterface.class);
            Call<JsonObject> call = apiService.user_register(jsonObject);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.e(TAG, "UpdateFCMTask: Response >> " + response.body().toString());
                    String resp = response.body().toString();
                    try {
                        JSONObject jsonObject = new JSONObject(resp);
                        if (jsonObject.getString("status").equalsIgnoreCase("200")) {

                        } else if (jsonObject.getString("status").equalsIgnoreCase("400")) {

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                }
            });
            return null;
        }
    }

    private void setFloatingButton() {
        new FloatingButton().showFloatingButton(MainActivity.this);
        new FloatingButton().setFloatingButtonControls(MainActivity.this);
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
                        ? R.drawable.back_light_new : R.drawable.add_light_new);
            }
        });

        set.play(scaleOutX).with(scaleOutY);
        set.play(scaleInX).with(scaleInY).after(scaleOutX);
        set.setInterpolator(new OvershootInterpolator(2));
        floatingActionMenu.setIconToggleAnimatorSet(set);
    }

    public class FloatingButton {
        FrameLayout bckgroundDimmer;
        FloatingActionButton fabHome, fabSearch, fabCallOrder, fabUpload, fabMyPresc;

        public void showFloatingButton(Activity activity) {

            floatingActionMenu = (FloatingActionMenu) activity.findViewById(R.id.material_design_android_floating_action_menu);
            fabHome = (FloatingActionButton) activity.findViewById(R.id.fabHome);
            fabSearch = (FloatingActionButton) activity.findViewById(R.id.fabSearch);
            fabCallOrder = (FloatingActionButton) activity.findViewById(R.id.fabCallOrder);
            fabUpload = (FloatingActionButton) activity.findViewById(R.id.fabUpload);
            fabMyPresc = (FloatingActionButton) activity.findViewById(R.id.fabMyPresc);
            createCustomAnimation();

            fabHome.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    floatingActionMenu.close(true);
                    FragmentManager manager = getSupportFragmentManager();
                    FragmentTransaction ft = manager.beginTransaction();
                    Fragment fragment = new HomeFragment();
                    ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                    ft.replace(R.id.main_container, fragment);
                    ft.commitAllowingStateLoss();
                    clearBackStack();
                }
            });

            fabCallOrder.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    floatingActionMenu.close(true);
                    Functions.callOrder(context);
                }
            });

            fabSearch.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    floatingActionMenu.close(true);
                    startActivity(new Intent(context, SearchActivity.class));
                }
            });

            fabUpload.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    floatingActionMenu.close(true);
                    FragmentManager manager = getSupportFragmentManager();
                    FragmentTransaction ft = manager.beginTransaction();
                    Fragment fragment = new AddPrescrFragment();
                    ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                    ft.replace(R.id.main_container, fragment);
                    ft.addToBackStack(null);
                    ft.commitAllowingStateLoss();
                }
            });

            fabMyPresc.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    floatingActionMenu.close(true);
                    FragmentManager manager = getSupportFragmentManager();
                    FragmentTransaction ft = manager.beginTransaction();
                    Fragment fragment = new MyPrescriptionFragment();
                    ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                    ft.replace(R.id.main_container, fragment);
                    ft.addToBackStack(null);
                    ft.commitAllowingStateLoss();
                }
            });
        }

        public void setFloatingButtonControls(Activity activity) {
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

}
