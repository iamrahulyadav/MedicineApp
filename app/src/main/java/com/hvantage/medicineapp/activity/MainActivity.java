package com.hvantage.medicineapp.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hvantage.medicineapp.R;
import com.hvantage.medicineapp.activity.business.BusinessLoginActivity;
import com.hvantage.medicineapp.database.DBHelper;
import com.hvantage.medicineapp.fragments.CartFragment;
import com.hvantage.medicineapp.fragments.HomeFragment;
import com.hvantage.medicineapp.fragments.UploadPrecriptionFragment;
import com.hvantage.medicineapp.fragments.VaultFragment;
import com.hvantage.medicineapp.model.DrugModel;
import com.hvantage.medicineapp.util.AppConstants;
import com.hvantage.medicineapp.util.FragmentIntraction;
import com.hvantage.medicineapp.util.Functions;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, FragmentIntraction {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_ALL_PERMISSIONS = 100;
    private TextView toolbar_title;
    private FirebaseAuth auth;
    private Context context;
    private NavigationView navigationView;
    private TextView tvLogin, tvUsername;

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

        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            hideMenus();
            tvUsername.setVisibility(View.GONE);
            tvLogin.setVisibility(View.VISIBLE);
        } else {
            tvUsername.setVisibility(View.VISIBLE);
            tvLogin.setVisibility(View.GONE);
            Log.e(TAG, "onCreate: uid >> " + auth.getCurrentUser().getUid());
            Log.e(TAG, "onCreate: name >> " + auth.getCurrentUser().getDisplayName());
            Log.e(TAG, "onCreate: phone >> " + auth.getCurrentUser().getPhoneNumber());
            tvUsername.setText("Hello, " + auth.getCurrentUser().getDisplayName());
        }

        if (new DBHelper(context).getMedicines() == null)
            getData();


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

    private void getData() {
        if (Functions.isConnectingToInternet(context)) {
            Log.e(TAG, "getDataFromServer: deleteMedicineData() >> " + new DBHelper(context).deleteMedicineData());
            FirebaseDatabase.getInstance().getReference()
                    .child(AppConstants.APP_NAME)
                    .child(AppConstants.FIREBASE_KEY.MEDICINE)
                    .orderByChild("name")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                DrugModel data = postSnapshot.getValue(DrugModel.class);
                                new DBHelper(context).saveMedicine(data);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.w(TAG, "deleteMedicineData:onCancelled", databaseError.toException());

                        }
                    });
        } else {
            Toast.makeText(context, "No internet connection", Toast.LENGTH_LONG).show();
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

    private void openCartFragment() {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.replace(R.id.main_container, new CartFragment());
        ft.addToBackStack(null);
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
                startActivity(new Intent(context, LoginActivity.class));
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_cart:
                openCartFragment();
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
        Fragment fragment;
        switch (id) {
            case R.id.nav_home:
                fragment = new HomeFragment();
                ft.replace(R.id.main_container, fragment);
                ft.commitAllowingStateLoss();
                clearBackStack();
                break;
            case R.id.nav_upload_pre:
                fragment = new UploadPrecriptionFragment();
                ft.replace(R.id.main_container, fragment);
                ft.addToBackStack(null);
                ft.commitAllowingStateLoss();
                break;
            case R.id.nav_vault:
                fragment = new VaultFragment();
                ft.replace(R.id.main_container, fragment);
                ft.addToBackStack(null);
                ft.commitAllowingStateLoss();
                break;
            case R.id.nav_business:
                startActivity(new Intent(context, BusinessLoginActivity.class));
                break;
            case R.id.nav_logout:
                logoutAlert();
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(context, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
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
}
