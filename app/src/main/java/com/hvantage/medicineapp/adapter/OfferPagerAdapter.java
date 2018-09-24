package com.hvantage.medicineapp.adapter;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hvantage.medicineapp.R;

import java.util.ArrayList;

public class OfferPagerAdapter extends android.support.v4.view.PagerAdapter {
    String TAG = "PagerAdapter";
    Context context;
    ArrayList<String> imageList;
    private LayoutInflater inflater;


    public OfferPagerAdapter(Context context, ArrayList<String> imageList) {
        this.context = context;
        this.imageList = imageList;
    }

    @Override
    public int getCount() {
        return imageList.size();
    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        ImageView imgDisplay;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.offer_pager_image_layout, container, false);
        imgDisplay = (ImageView) viewLayout.findViewById(R.id.imgDisplay);
        loadImage(imageList.get(position), imgDisplay);
        ((ViewPager) container).addView(viewLayout);
        return viewLayout;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);
    }

    private void loadImage(String url, ImageView imgDisplay) {
        Log.e(TAG, "loadImage: url >> " + url);
        Glide.with(context)
                .load(url)
                .crossFade()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgDisplay);
    }

    @Override
    public int getItemPosition(Object object) {
        return android.support.v4.view.PagerAdapter.POSITION_NONE;
    }
}