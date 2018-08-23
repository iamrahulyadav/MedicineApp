package com.hvantage.medicineapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;

import com.hvantage.medicineapp.R;
import com.hvantage.medicineapp.model.DoseData;

import java.util.ArrayList;
import java.util.List;

public class DialogMultipleChoiceAdapter extends BaseAdapter {
    LayoutInflater mLayoutInflater;
    List<DoseData> mItemList;

    public DialogMultipleChoiceAdapter(Context context, List<DoseData> itemList) {
        mLayoutInflater = LayoutInflater.from(context);
        mItemList = itemList;
    }

    @Override
    public int getCount() {
        return mItemList.size();
    }

    @Override
    public DoseData getItem(int i) {
        return mItemList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public List<String> getSelectedItemIds() {
        List<String> checkedItemList = new ArrayList<>();
        for (DoseData item : mItemList) {
            if (item.isSelected()) {
                checkedItemList.add(item.getDose_id());
            }
        }
        return checkedItemList;
    }

    public List<String> getSelectedItemNames() {
        int counter = 0;
        List<String> checkedItemList = new ArrayList<>();
        for (DoseData item : mItemList) {
            if (item.isSelected()) {
                checkedItemList.add(item.getDose());
            }
        }
        return checkedItemList;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (mItemList.get(position).isSelected())
            holder.checkbox.setChecked(true);
        else
            holder.checkbox.setChecked(false);
        holder.checkbox.setText(mItemList.get(position).getDose());
        holder.checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mItemList.get(position).isSelected()) {
                    mItemList.get(position).setSelected(false);
                    holder.checkbox.setChecked(false);
                } else {
                    mItemList.get(position).setSelected(true);
                    holder.checkbox.setChecked(true);
                }
            }
        });

        return convertView;
    }

    private void updateItemState(ViewHolder holder, boolean checked) {
        holder.checkbox.setChecked(checked);
    }

    private static class ViewHolder {
        View root;
        CheckBox checkbox;

        ViewHolder(View view) {
            root = view;
            checkbox = view.findViewById(R.id.checkbox);
        }
    }


}
