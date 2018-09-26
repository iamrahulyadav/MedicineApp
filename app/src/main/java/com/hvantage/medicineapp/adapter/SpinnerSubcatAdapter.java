package com.hvantage.medicineapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.hvantage.medicineapp.R;
import com.hvantage.medicineapp.model.SubCategoryData;
import com.hvantage.medicineapp.model.SubCategoryData;

import java.util.List;

public class SpinnerSubcatAdapter extends ArrayAdapter<SubCategoryData> {
    LayoutInflater flater;

    public SpinnerSubcatAdapter(Activity context, int resouceId, int textviewId, List<SubCategoryData> list) {
        super(context, resouceId, textviewId, list);
        flater = context.getLayoutInflater();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        return rowview(convertView, position);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return rowview(convertView, position);
    }

    private View rowview(View convertView, int position) {

        SubCategoryData rowItem = getItem(position);

        viewHolder holder;
        View rowview = convertView;
        if (rowview == null) {

            holder = new viewHolder();
            flater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowview = flater.inflate(R.layout.spinner_cat_item_layout, null, false);

            holder.txtTitle = (AppCompatTextView) rowview.findViewById(R.id.tvTitle);
            rowview.setTag(holder);
        } else {
            holder = (viewHolder) rowview.getTag();
        }
        holder.txtTitle.setText(rowItem.getSubCatName());

        return rowview;
    }

    private class viewHolder {
        AppCompatTextView txtTitle;
    }


}
