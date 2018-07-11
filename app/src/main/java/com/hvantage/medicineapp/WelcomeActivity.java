package com.hvantage.medicineapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener {

    private ViewPager mPager;
    private int[] layouts = {R.layout.page0, R.layout.page1, R.layout.page2, R.layout.page3, R.layout.page4};
    private MpagerAdapter mpagerAdapter;
    private LinearLayout dots_layout;
    private ImageView[] dots;
    private TextView BnNext;
    private TextView BnSkip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*if (new PreferenceManager(this).checkPreference())
        {
            loadHome();
        }*/
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_slide_welcome);
        mPager = (ViewPager) findViewById(R.id.viewPager);
        mpagerAdapter = new MpagerAdapter(layouts, this);
        mPager.setAdapter(mpagerAdapter);
        dots_layout = (LinearLayout) findViewById(R.id.dotslayout);
        BnNext = (TextView) findViewById(R.id.bnNext);
        BnSkip = (TextView) findViewById(R.id.bnSkip);
        BnNext.setOnClickListener(this);
        BnSkip.setOnClickListener(this);

        BnSkip.setVisibility(View.INVISIBLE);
        createDots(0);
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                createDots(position);
                if (position == 0) {
                    BnSkip.setVisibility(View.INVISIBLE);
                } else if (layouts.length == 1) {
                    BnSkip.setVisibility(View.VISIBLE);
                } else if (position == layouts.length - 1) {
                    BnNext.setText("Done");
                    BnSkip.setVisibility(View.INVISIBLE);
                } else {
                    BnNext.setText("Next");
                    BnSkip.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void createDots(int current_position) {
        if (dots_layout != null) {
            dots_layout.removeAllViews();
            dots = new ImageView[layouts.length];
            for (int i = 0; i < layouts.length; i++) {
                dots[i] = new ImageView(this);
                if (i == current_position) {
                    dots[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.active_dots));
                } else {
                    dots[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.default_dots));
                }

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(4, 0, 4, 0);

                dots_layout.addView(dots[i], params);
            }

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bnNext:
                loadNextSlide();
                break;
            case R.id.bnSkip:
                loadHome();
//                new PreferenceManager(this).writePreference();
                break;
        }

    }

    private void loadHome() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private void loadNextSlide() {
        int next_slide = mPager.getCurrentItem() + 1;
        if (next_slide < layouts.length) {
            mPager.setCurrentItem(next_slide);
        } else {
            loadHome();
            //new PreferenceManager(this).writePreference();
        }
    }

}
