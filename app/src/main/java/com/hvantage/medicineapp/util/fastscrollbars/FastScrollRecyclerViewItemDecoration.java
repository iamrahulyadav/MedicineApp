package com.hvantage.medicineapp.util.fastscrollbars;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;

import com.hvantage.medicineapp.R;

/**
 * Created by flaviusmester on 23/02/15.
 */
public class FastScrollRecyclerViewItemDecoration extends RecyclerView.ItemDecoration {
    private Context mContext;
    private Typeface typeface;

    public FastScrollRecyclerViewItemDecoration(Context context) {
        mContext = context;
    }

    @Override
    public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(canvas, parent, state);
        typeface = ResourcesCompat.getFont(mContext, R.font.quicksand);

        float scaledWidth = ((FastScrollRecyclerView) parent).scaledWidth;
        float sx = ((FastScrollRecyclerView) parent).sx;
        float scaledHeight = ((FastScrollRecyclerView) parent).scaledHeight;
        float sy = ((FastScrollRecyclerView) parent).sy;
        String[] sections = ((FastScrollRecyclerView) parent).sections;
        String section = ((FastScrollRecyclerView) parent).section;
        boolean showLetter = ((FastScrollRecyclerView) parent).showLetter;

        // We draw the letter in the middle
        if (showLetter & section != null && !section.equals("")) {
            //overlay everything when displaying selected index Letter in the middle
            Paint overlayDark = new Paint();
            overlayDark.setColor(mContext.getResources().getColor(R.color.colorWhite));
            overlayDark.setAlpha(180);
            canvas.drawRect(0, 0, parent.getWidth(), parent.getHeight(), overlayDark);
            float middleTextSize = mContext.getResources().getDimension(R.dimen.fast_scroll_overlay_text_size);
            Paint middleLetter = new Paint();
            middleLetter.setColor(mContext.getResources().getColor(R.color.colorPrimary));
            middleLetter.setTextSize(middleTextSize);
            middleLetter.setAntiAlias(true);
            middleLetter.setFakeBoldText(true);
            //middleLetter.setStyle(Paint.Style.FILL);
            middleLetter.setTypeface(typeface);
            int xPos = (canvas.getWidth() - (int) middleTextSize) / 2;
            int yPos = (int) ((canvas.getHeight() / 2) - ((middleLetter.descent() + middleLetter.ascent()) / 2));


            canvas.drawText(section.toUpperCase(), xPos, yPos, middleLetter);
        }
        // draw indez A-Z

        Paint textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setStyle(Paint.Style.FILL);

        for (int i = 0; i < sections.length; i++) {
            if (showLetter & section != null && !section.equals("") && section != null
                    && sections[i].toUpperCase().equals(section.toUpperCase())) {
                textPaint.setColor(mContext.getResources().getColor(R.color.colorPrimary));
                textPaint.setAlpha(255);
                textPaint.setTextSize((float) (scaledWidth / 1.8));
                textPaint.setTypeface(typeface);
                canvas.drawText(sections[i].toUpperCase(),
                        sx + textPaint.getTextSize() / 1, sy + parent.getPaddingTop()
                                + scaledHeight * (i + 1), textPaint);
                textPaint.setTextSize((float) (scaledWidth));
                canvas.drawText("•  ",
                        sx - textPaint.getTextSize() / 4, sy + parent.getPaddingTop()
                                + scaledHeight * (i + 1) + scaledHeight / 3, textPaint);

            } else {
                textPaint.setColor(mContext.getResources().getColor(R.color.colorTextSubHeading));
                textPaint.setAlpha(200);
                textPaint.setTextSize((float) (scaledWidth / 1.8));
                textPaint.setTypeface(typeface);
                canvas.drawText(sections[i].toUpperCase(),
                        sx + textPaint.getTextSize() / 1, sy + parent.getPaddingTop()
                                + scaledHeight * (i + 1), textPaint);
            }

        }


    }
}
