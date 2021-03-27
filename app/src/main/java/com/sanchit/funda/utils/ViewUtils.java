package com.sanchit.funda.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.sanchit.funda.R;
import com.sanchit.funda.cache.CacheManager;
import com.sanchit.funda.cache.Caches;

import java.math.BigDecimal;

public class ViewUtils {

    public static void setRecyclerViewItemLayoutParams(View layoutView) {
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, //width
                ViewGroup.LayoutParams.WRAP_CONTENT); //height
        layoutView.setLayoutParams(lp);//override default layout params
    }

    public static void setLeftMarginOnConstraintLayout(Context context, View textViewCurrent, int margin) {
        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) textViewCurrent.getLayoutParams();
        lp.setMargins(margin, 0, 0, 0);
        textViewCurrent.setLayoutParams(lp);
    }

    public static void setLeftMarginOnLinearLayout(Context context, View textViewCurrent, int margin) {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) textViewCurrent.getLayoutParams();
        lp.setMargins(margin, 0, 0, 0);
        textViewCurrent.setLayoutParams(lp);
    }

    public static int toPixel(Context context, int dp) {
        Resources r = context.getResources();
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
        return px;
    }

    public static int getWidth(View view) {
        CacheManager.Cache<String, BigDecimal> cache = CacheManager.getOrRegisterCache(Caches.VIEW_DATA, BigDecimal.class);
        String viewWidthKey = view.getId() + "-Width";
        if (cache.exists(viewWidthKey)) {
            return cache.get(viewWidthKey).intValue();
        }
        int width = view.getMeasuredWidth();
        if (width > 0) {
            cache.add(viewWidthKey, new BigDecimal(width));
        }
        return width;
    }

    public static void setActionBarColor(AppCompatActivity activity, int color) {
        ActionBar actionBar = activity.getSupportActionBar();
        ColorDrawable colorDrawable = new ColorDrawable(activity.getResources().getColor(R.color.colorPrimaryDark));
        actionBar.setBackgroundDrawable(colorDrawable);
    }

    public static void setTextViewData(Activity activity, int viewId, String fundName) {
        TextView view = activity.findViewById(viewId);
        view.setText(fundName);
    }
}
