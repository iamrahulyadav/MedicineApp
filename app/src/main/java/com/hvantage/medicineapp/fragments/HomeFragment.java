package com.hvantage.medicineapp.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hvantage.medicineapp.R;
import com.hvantage.medicineapp.activity.ProductDetailActivity;
import com.hvantage.medicineapp.adapter.CategoryAdapter;
import com.hvantage.medicineapp.adapter.HomeProductAdapter;
import com.hvantage.medicineapp.model.CategoryModel;
import com.hvantage.medicineapp.model.ProductModel;
import com.hvantage.medicineapp.util.AppConstants;
import com.hvantage.medicineapp.util.FragmentIntraction;
import com.hvantage.medicineapp.util.RecyclerItemClickListener;

import java.util.ArrayList;


public class HomeFragment extends Fragment implements View.OnClickListener {
    private static final int REQUEST_ALL_PERMISSIONS = 100;
    ArrayList<CategoryModel> catList = new ArrayList<CategoryModel>();
    ArrayList<ProductModel> productList = new ArrayList<ProductModel>();
    ArrayList<ProductModel> productList2 = new ArrayList<ProductModel>();
    private RecyclerView recylcer_view;
    private CategoryAdapter adapter;
    private RecyclerView recylcer_view2;
    private HomeProductAdapter adapter2;
    private RecyclerView recylcer_view3;
    private HomeProductAdapter adapter3;
    private Context context;
    private View rootView;
    private FragmentIntraction intraction;
    private CardView btnUpload;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = container.getContext();
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        if (intraction != null) {
            intraction.actionbarsetTitle(getResources().getString(R.string.app_name));
        }
        init();
        setCategory();
        setProduct();
        setProduct2();
        return rootView;
    }

    private void init() {
        btnUpload = (CardView) rootView.findViewById(R.id.btnUpload);
        btnUpload.setOnClickListener(this);
    }

    private void setCategory() {
        catList.add(new CategoryModel(1, "Prescriptions", R.drawable.cat_prescription));
        catList.add(new CategoryModel(1, AppConstants.CATEGORY.OTC, R.drawable.cat_otc));
        catList.add(new CategoryModel(1, AppConstants.CATEGORY.PERSONAL_CARE, R.drawable.cat_personal_care));
        catList.add(new CategoryModel(1, "Diabetes", R.drawable.cat_diabetes));
        catList.add(new CategoryModel(1, "Baby & Mother", R.drawable.cat_baby_mother));
        catList.add(new CategoryModel(1, "Wellness", R.drawable.cat_wellness));
        catList.add(new CategoryModel(1, "Health Aid", R.drawable.cat_aid));
        catList.add(new CategoryModel(1, "Ayurvedic", R.drawable.cat_ayurvedic));
        catList.add(new CategoryModel(1, "Homeopathy", R.drawable.cat_homeo));

        recylcer_view = (RecyclerView) rootView.findViewById(R.id.recylcer_view);
        adapter = new CategoryAdapter(context, catList);
        recylcer_view.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        recylcer_view.setAdapter(adapter);
        recylcer_view.addOnItemTouchListener(new RecyclerItemClickListener(context, recylcer_view, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                BrowseCategoryFragment fragment = new BrowseCategoryFragment();
                Bundle args = new Bundle();
                args.putString("data", catList.get(position).getName());
                fragment.setArguments(args);
                FragmentManager manager = getFragmentManager();
                FragmentTransaction ft = manager.beginTransaction();
                ft.replace(R.id.main_container, fragment);
                ft.addToBackStack(null);
                ft.commitAllowingStateLoss();
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));

        adapter.notifyDataSetChanged();
    }

    private void setProduct() {
        recylcer_view2 = (RecyclerView) rootView.findViewById(R.id.recylcer_view2);
        adapter2 = new HomeProductAdapter(context, productList);
        recylcer_view2.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        recylcer_view2.setAdapter(adapter2);
        recylcer_view2.addOnItemTouchListener(new RecyclerItemClickListener(context, recylcer_view2, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                startActivity(new Intent(context, ProductDetailActivity.class));
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
        recylcer_view3 = (RecyclerView) rootView.findViewById(R.id.recylcer_view3);
        adapter3 = new HomeProductAdapter(context, productList2);
        recylcer_view3.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        recylcer_view3.setAdapter(adapter3);
        recylcer_view3.addOnItemTouchListener(new RecyclerItemClickListener(context, recylcer_view2, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                startActivity(new Intent(context, ProductDetailActivity.class));
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
                FragmentManager manager = getFragmentManager();
                FragmentTransaction ft = manager.beginTransaction();
                ft.replace(R.id.main_container, new UploadPrecriptionFragment());
                ft.addToBackStack(null);
                ft.commitAllowingStateLoss();
                break;
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


}
