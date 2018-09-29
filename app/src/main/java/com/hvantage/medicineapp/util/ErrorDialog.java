package com.hvantage.medicineapp.util;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;

import com.hvantage.medicineapp.R;

public class ErrorDialog {
    public static void setDialog(AppCompatActivity activity, String error_msg, DialogInterface.OnClickListener clickListener) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        dialog.setCancelable(true);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_error, null);
        AppCompatTextView tvMsg = dialogView.findViewById(R.id.tvMsg);
        AppCompatButton btnRetry = dialogView.findViewById(R.id.btnRetry);
        dialog.setView(dialogView);
        tvMsg.setText(error_msg);
        btnRetry.setOnClickListener((View.OnClickListener) clickListener);
        dialog.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        dialog.show();
    }

}
