package com.hvantage.medicineapp.services;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hvantage.medicineapp.database.DBHelper;
import com.hvantage.medicineapp.model.DrugModel;
import com.hvantage.medicineapp.util.AppConstants;
import com.hvantage.medicineapp.util.Functions;

public class DBService extends Service {
    private static final String TAG = "DBService";
    private DatabaseReference mDatabase;
    private DBHelper mydb;

    public DBService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Toast.makeText(getApplicationContext(), "Service started", Toast.LENGTH_SHORT).show();
        new SynchronizeData().execute();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //  Toast.makeText(getApplicationContext(), "Service stopped", Toast.LENGTH_SHORT).show();
    }

    class SynchronizeData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mydb = new DBHelper(getApplicationContext());
            Log.e("Db service : ", "OnPostExecute");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Log.e("Db service : ", "doInBack..");
            if (Functions.isConnectingToInternet(getApplicationContext())) {
                Log.e(TAG, "getDataFromServer: deleteMedicineData() >> " + mydb.deleteMedicineData());
                FirebaseDatabase.getInstance().getReference()
                        .child(AppConstants.APP_NAME)
                        .child(AppConstants.FIREBASE_KEY.MEDICINE)
                        .orderByChild("name")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                    DrugModel data = postSnapshot.getValue(DrugModel.class);
                                    mydb.saveMedicine(data);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.e(TAG, "deleteMedicineData:onCancelled", databaseError.toException());
                            }
                        });
            } else {
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
}
