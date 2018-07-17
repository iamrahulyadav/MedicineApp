package com.hvantage.medicineapp.activity.business;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hvantage.medicineapp.R;
import com.hvantage.medicineapp.model.DrugModel;
import com.hvantage.medicineapp.util.AppConstants;
import com.hvantage.medicineapp.util.ProgressBar;
import com.hvantage.medicineapp.util.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.Locale;

public class BusinessHomeActivity extends AppCompatActivity {

    private static final String TAG = "BusinessHomeActivity";
    private static final int REQ_CODE_SPEECH_INPUT = 100;
    ArrayList<DrugModel> filteredList = new ArrayList<DrugModel>();
    private FloatingActionButton fabAdd;
    private RecyclerView recylcer_view;
    private ArrayList<DrugModel> list = new ArrayList<DrugModel>();
    private Context context;
    private BusinessProductAdapter adapter;
    private ProgressBar progressBar;
    private CardView cardEmptyText;
    private EditText etSearch;
    private ImageView btnVoiceInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business);
        context = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();
        getData();
        setRecyclerView(filteredList);
    }

    private void getData() {
        showProgressDialog();
        FirebaseDatabase.getInstance().getReference()
                .child(AppConstants.APP_NAME)
                .child(AppConstants.FIREBASE_KEY.MEDICINE)
                .orderByChild("name")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        list.clear();
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            DrugModel data = postSnapshot.getValue(DrugModel.class);
                            Log.e(TAG, "onDataChange: data >> " + data);
                            list.add(data);
                            filteredList.add(data);
                        }
                        adapter.notifyDataSetChanged();
                        if (adapter.getItemCount() > 0)
                            cardEmptyText.setVisibility(View.GONE);
                        else
                            cardEmptyText.setVisibility(View.VISIBLE);
                        hideProgressDialog();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                        if (adapter.getItemCount() > 0)
                            cardEmptyText.setVisibility(View.GONE);
                        else
                            cardEmptyText.setVisibility(View.VISIBLE);
                        hideProgressDialog();
                    }
                });
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

    private void setRecyclerView(final ArrayList<DrugModel> list1) {
        adapter = new BusinessProductAdapter(context, list1);
        recylcer_view.setLayoutManager(new LinearLayoutManager(context));
        recylcer_view.setAdapter(adapter);
        recylcer_view.addOnItemTouchListener(new RecyclerItemClickListener(context, recylcer_view, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(context, AddProductActivity.class);
                intent.putExtra("data", list1.get(position));
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));
        adapter.notifyDataSetChanged();
    }

    private void init() {
        etSearch = (EditText) findViewById(R.id.etSearch);
        btnVoiceInput = (ImageView) findViewById(R.id.btnVoiceInput);
        cardEmptyText = (CardView) findViewById(R.id.cardEmptyText);
        recylcer_view = (RecyclerView) findViewById(R.id.recylcer_view);
        fabAdd = (FloatingActionButton) findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BusinessHomeActivity.this, AddProductActivity.class));
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int k, int i1, int i2) {
                String query = charSequence.toString().toLowerCase();
                Log.e(TAG, "onTextChanged: query >> " + query);
                filteredList.clear();
               /* if (query.isEmpty()) {
                    filteredList = list;
                }*/
                for (int i = 0; i < list.size(); i++) {
                    final String name = list.get(i).getName().toLowerCase();
                    if (name.contains(query)) {
                        filteredList.add(list.get(i));
                    }
                }

                // data set changed
                //setRecyclerView(filteredList);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void afterTextChanged(Editable editable) {
                //filter(editable.toString());
            }
        });

        btnVoiceInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                promptSpeechInput();
            }
        });
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

    void filter(String text) {
        ArrayList<DrugModel> temp = new ArrayList();
        for (DrugModel d : list) {
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if (d.getName().toLowerCase().contains(text.toLowerCase())) {
                temp.add(d);
            }
        }
        adapter.updateList(temp);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}