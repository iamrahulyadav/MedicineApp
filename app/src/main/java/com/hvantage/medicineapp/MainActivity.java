package com.hvantage.medicineapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.hvantage.medicineapp.adapter.CategoryAdapter;
import com.hvantage.medicineapp.adapter.HomeProductAdapter;
import com.hvantage.medicineapp.model.CategoryModel;
import com.hvantage.medicineapp.model.ProductModel;
import com.hvantage.medicineapp.util.RecyclerItemClickListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ArrayList<CategoryModel> catList = new ArrayList<CategoryModel>();
    ArrayList<ProductModel> productList = new ArrayList<ProductModel>();
    ArrayList<ProductModel> productList2 = new ArrayList<ProductModel>();
    private RecyclerView recylcer_view;
    private CategoryAdapter adapter;
    private RecyclerView recylcer_view2;
    private HomeProductAdapter adapter2;
    private RecyclerView recylcer_view3;
    private HomeProductAdapter adapter3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initDrawer(toolbar);

    }

    private void setCategory() {
        recylcer_view = (RecyclerView) findViewById(R.id.recylcer_view);
        adapter = new CategoryAdapter(MainActivity.this, catList);
        recylcer_view.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
        recylcer_view.setAdapter(adapter);

        catList.add(new CategoryModel(1, "Prescriptions", R.drawable.cat_prescription));
        catList.add(new CategoryModel(1, "OTC", R.drawable.cat_otc));
        catList.add(new CategoryModel(1, "Diabetes", R.drawable.cat_diabetes));
        catList.add(new CategoryModel(1, "Baby & Mother", R.drawable.cat_baby_mother));
        catList.add(new CategoryModel(1, "Personal Care", R.drawable.cat_personal_care));
        catList.add(new CategoryModel(1, "Wellness", R.drawable.cat_wellness));
        catList.add(new CategoryModel(1, "Health Aid", R.drawable.cat_aid));
        catList.add(new CategoryModel(1, "Ayurvedic", R.drawable.cat_ayurvedic));
        catList.add(new CategoryModel(1, "Homeopathy", R.drawable.cat_homeo));
        adapter.notifyDataSetChanged();


    }

    private void initDrawer(Toolbar toolbar) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        setCategory();
        setProduct();
        setProduct2();

    }

    private void setProduct() {
        recylcer_view2 = (RecyclerView) findViewById(R.id.recylcer_view2);
        adapter2 = new HomeProductAdapter(MainActivity.this, productList);
        recylcer_view2.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
        recylcer_view2.setAdapter(adapter2);
        recylcer_view2.addOnItemTouchListener(new RecyclerItemClickListener(MainActivity.this, recylcer_view, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                startActivity(new Intent(MainActivity.this, ProductDetailActivity.class));
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));
        productList.add(new ProductModel("1", "Horlicks Chocolate Delight", "199", "500gm", ""));
        productList.add(new ProductModel("1", "Horlicks Chocolate Delight", "199", "500gm", ""));
        productList.add(new ProductModel("1", "Horlicks Chocolate Delight", "199", "500gm", ""));
        productList.add(new ProductModel("1", "Horlicks Chocolate Delight", "199", "500gm", ""));
        productList.add(new ProductModel("1", "Horlicks Chocolate Delight", "199", "500gm", ""));
        adapter2.notifyDataSetChanged();

    }

    private void setProduct2() {
        recylcer_view3 = (RecyclerView) findViewById(R.id.recylcer_view3);
        adapter3 = new HomeProductAdapter(MainActivity.this, productList2);
        recylcer_view3.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
        recylcer_view3.setAdapter(adapter3);
        recylcer_view3.addOnItemTouchListener(new RecyclerItemClickListener(MainActivity.this, recylcer_view, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                startActivity(new Intent(MainActivity.this, ProductDetailActivity.class));
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));
        productList2.add(new ProductModel("2", "Colgate Total Advanced Health Tooth Paste", "80.75", "120 gm", ""));
        productList2.add(new ProductModel("2", "Colgate Total Advanced Health Tooth Paste", "80.75", "120 gm", ""));
        productList2.add(new ProductModel("2", "Colgate Total Advanced Health Tooth Paste", "80.75", "120 gm", ""));
        productList2.add(new ProductModel("2", "Colgate Total Advanced Health Tooth Paste", "80.75", "120 gm", ""));
        adapter3.notifyDataSetChanged();
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

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
