package com.sanchit.funda.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class ViewUtils {

    public static void setRecyclerViewItemLayoutParams(View layoutView) {
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, //width
                ViewGroup.LayoutParams.WRAP_CONTENT); //height
        layoutView.setLayoutParams(lp);//override default layout params
    }

    public static void setLeftMarginOnConstrainLayout(Context context, View textViewCurrent, int margin) {
        ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(margin, 0, 0, 0);
        lp = (ConstraintLayout.LayoutParams) textViewCurrent.getLayoutParams();
        lp.setMargins(toPixel(context, margin), 0, 0, 0);
        textViewCurrent.setLayoutParams(lp);
    }

    private static int toPixel(Context context, int dp) {
        Resources r = context.getResources();
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
        return px;
    }
}
