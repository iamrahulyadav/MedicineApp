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
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hvantage.medicineapp.R;
import com.hvantage.medicineapp.activity.LoginActivity;
import com.hvantage.medicineapp.activity.ProductDetailActivity;
import com.hvantage.medicineapp.adapter.CategoryAdapter;
import com.hvantage.medicineapp.adapter.HomeProductAdapter;
import com.hvantage.medicineapp.adapter.HomeProductAdapter2;
import com.hvantage.medicineapp.database.DBHelper;
import com.hvantage.medicineapp.model.CategoryModel;
import com.hvantage.medicineapp.model.ProductModel;
import com.hvantage.medicineapp.util.AppConstants;
import com.hvantage.medicineapp.util.FragmentIntraction;
import com.hvantage.medicineapp.util.Functions;
import com.hvantage.medicineapp.util.ProgressBar;
import com.hvantage.medicineapp.util.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;


public class HomeFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "HomeFragment";
    private static final int REQUEST_ALL_PERMISSIONS = 100;
    private final int REQ_CODE_SPEECH_INPUT = 101;
    ArrayList<CategoryModel> catList = new ArrayList<CategoryModel>();
    ArrayList<ProductModel> productList = new ArrayList<ProductModel>();
    ArrayList<ProductModel> listDailyNeeds = new ArrayList<ProductModel>();
    private RecyclerView recylcer_view;
    private CategoryAdapter adapter;
    private HomeProductAdapter adapterRecco;
    private RecyclerView recylcer_view_daily;
    private HomeProductAdapter adapterDaily;
    private Context context;
    private View rootView;
    private FragmentIntraction intraction;
    private CardView btnUpload;
    private ImageView btnVoiceInput;
    private AppCompatAutoCompleteTextView etSearch;
    private ArrayList<String> list;
    private ProgressBar progressBar;
    private RecyclerView recylcer_view_recco;
    private RecyclerView recylcer_view_daily2;
    private HomeProductAdapter2 adapterDaily2;
    private FloatingActionMenu floatingActionMenu;

    private void setFloatingButton() {
        new FloatingButton().showFloatingButton(rootView, context);
        new FloatingButton().setFloatingButtonControls(rootView);
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

        list = new DBHelper(context).getMedicinesSearch();
//        catArray = getResources().getStringArray(R.array.categories);
//        Log.e(TAG, "onCreateView: catArray >> " + catArray);
//        randomCat = catArray[new Random().nextInt(catArray.length)];
//        Log.e(TAG, "onCreateView: randomCat >> " + randomCat);

        setCategory();
        //setProduct();
        setRecylclerviewDaily();
        setRecylclerviewDaily2();
        setSearchBar();
        getRandomCatData();
        return rootView;
    }

    private void getRandomCatData() {
        showProgressDialog();
        FirebaseDatabase.getInstance().getReference()
                .child(AppConstants.APP_NAME)
                .child(AppConstants.FIREBASE_KEY.MEDICINE)
                .orderByChild("category_name")
                .equalTo("Personal Care")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        listDailyNeeds.clear();
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            ProductModel model = postSnapshot.getValue(ProductModel.class);
                            Log.d(TAG, "onDataChange: model >> " + model);
                            listDailyNeeds.add(model);
                            /*if (listDailyNeeds.size() == 10)
                                break;*/
                        }
                        adapterDaily.notifyDataSetChanged();
                        adapterDaily2.notifyDataSetChanged();
                        hideProgressDialog();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, "loadPost:onCancelled", databaseError.toException());
                        hideProgressDialog();
                    }
                });
    }

    private void setSearchBar() {
        if (list != null) {
            Log.e(TAG, "setSearchBar: list >> " + list.size());
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.auto_complete_text, list);
            etSearch.setThreshold(1);
            etSearch.setAdapter(adapter);
            etSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Log.e(TAG, "onItemClick: text >> " + etSearch.getText().toString());
                    if (Functions.isConnectingToInternet(context)) {
                        showProgressDialog();
                        FirebaseDatabase.getInstance().getReference()
                                .child(AppConstants.APP_NAME)
                                .child(AppConstants.FIREBASE_KEY.MEDICINE)
                                .orderByChild("name")
                                .equalTo(etSearch.getText().toString())
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        hideProgressDialog();
                                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                            ProductModel data = postSnapshot.getValue(ProductModel.class);
                                            Log.e(TAG, "onDataChange: data >> " + data);
                                            startActivity(new Intent(context, ProductDetailActivity.class).putExtra("medicine_data", data));
                                            etSearch.setText("");
                                            break;
                                        }


                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        hideProgressDialog();
                                        Log.w(TAG, "onCancelled >> ", databaseError.toException());
                                    }
                                });
                    } else {
                        Toast.makeText(context, "No internet connection", Toast.LENGTH_LONG).show();
                    }
                }
            });
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


    private void init() {
        etSearch = (AppCompatAutoCompleteTextView) rootView.findViewById(R.id.etSearch);
        btnUpload = (CardView) rootView.findViewById(R.id.btnUpload);
        btnVoiceInput = (ImageView) rootView.findViewById(R.id.btnVoiceInput);
        btnUpload.setOnClickListener(this);
        btnVoiceInput.setOnClickListener(this);
        ((ScrollView) rootView.findViewById(R.id.container)).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Functions.hideSoftKeyboard(context, view);
                return false;
            }
        });
    }

    private void setCategory() {
        catList.clear();
        catList.add(new CategoryModel(1, "Prescriptions", R.drawable.cat_prescription));
        catList.add(new CategoryModel(1, AppConstants.CATEGORY.OTC, R.drawable.cat_otc));
        catList.add(new CategoryModel(1, AppConstants.CATEGORY.PERSONAL_CARE, R.drawable.cat_personal_care));
        catList.add(new CategoryModel(1, AppConstants.CATEGORY.BABY_AND_MOTHER, R.drawable.cat_baby_mother));
        catList.add(new CategoryModel(1, AppConstants.CATEGORY.WELLNESS, R.drawable.cat_wellness));
        catList.add(new CategoryModel(1, "Diabetes", R.drawable.cat_diabetes));
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
                if (position == 0) {
                    AllPrescriptionFragment fragment = new AllPrescriptionFragment();
                    FragmentManager manager = getFragmentManager();
                    FragmentTransaction ft = manager.beginTransaction();
                    ft.replace(R.id.main_container, fragment);
                    ft.addToBackStack(null);
                    ft.commitAllowingStateLoss();
                } else {
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
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));

        adapter.notifyDataSetChanged();
    }

    private void setProduct() {
        recylcer_view_recco = (RecyclerView) rootView.findViewById(R.id.recylcer_view_recco);
        adapterRecco = new HomeProductAdapter(context, productList);
        recylcer_view_recco.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        recylcer_view_recco.setAdapter(adapterRecco);
        recylcer_view_recco.addOnItemTouchListener(new RecyclerItemClickListener(context, recylcer_view_recco, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                startActivity(new Intent(context, ProductDetailActivity.class));
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));
        adapterRecco.notifyDataSetChanged();
    }

    private void setRecylclerviewDaily() {
        recylcer_view_daily = (RecyclerView) rootView.findViewById(R.id.recylcer_view_daily);
        adapterDaily = new HomeProductAdapter(context, listDailyNeeds);
        recylcer_view_daily.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        recylcer_view_daily.setAdapter(adapterDaily);
        recylcer_view_daily.addOnItemTouchListener(new RecyclerItemClickListener(context, recylcer_view_daily, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
               /* startActivity(new Intent(context, ProductDetailActivity.class)
                        .putExtra("medicine_data", listDailyNeeds.get(position)));*/
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));
        adapterDaily.notifyDataSetChanged();
    }

    private void setRecylclerviewDaily2() {
        recylcer_view_daily2 = (RecyclerView) rootView.findViewById(R.id.recylcer_view_daily2);
        recylcer_view_daily2.setLayoutManager(new LinearLayoutManager(context));
        adapterDaily2 = new HomeProductAdapter2(context, listDailyNeeds);
        recylcer_view_daily2.setAdapter(adapterDaily2);
        recylcer_view_daily2.addOnItemTouchListener(new RecyclerItemClickListener(context, recylcer_view_daily, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
               /* startActivity(new Intent(context, ProductDetailActivity.class)
                        .putExtra("medicine_data", listDailyNeeds.get(position)));*/
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));
        adapterDaily2.notifyDataSetChanged();
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
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    FragmentManager manager = getFragmentManager();
                    FragmentTransaction ft = manager.beginTransaction();
                    ft.replace(R.id.main_container, new UploadPrecriptionFragment());
                    ft.addToBackStack(null);
                    ft.commitAllowingStateLoss();
                } else {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
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
