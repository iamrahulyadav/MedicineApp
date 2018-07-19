package com.hvantage.medicineapp.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.hvantage.medicineapp.R;
import com.hvantage.medicineapp.model.PrescriptionModel;
import com.hvantage.medicineapp.model.ProductModel;
import com.hvantage.medicineapp.util.Functions;
import com.hvantage.medicineapp.util.TouchImageView;

public class ImagePreviewActivity extends AppCompatActivity {
    private static final String TAG = "ImagePreviewActivity";
    private TouchImageView imageFull;
    private ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        imageFull = (TouchImageView) findViewById(R.id.imageFull);
        back = (ImageView) findViewById(R.id.back);
        if (getIntent().hasExtra("product_data")) {
            ProductModel data = (ProductModel) getIntent().getSerializableExtra("product_data");
            Log.d(TAG, "onCreate: data.getImage() >> " + data.getImage());
            imageFull.setImageBitmap(Functions.base64ToBitmap(data.getImage()));
        } else if (getIntent().hasExtra("prescription_data")) {
            PrescriptionModel data = (PrescriptionModel) getIntent().getSerializableExtra("prescription_data");
            Log.d(TAG, "onCreate: data.getImage() >> " + data.getImage_base64());
            imageFull.setImageBitmap(Functions.base64ToBitmap(data.getImage_base64()));
        }

        back.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                onBackPressed();
                return false;
            }
        });
    }
}
