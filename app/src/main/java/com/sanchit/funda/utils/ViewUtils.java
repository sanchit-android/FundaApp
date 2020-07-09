package com.sanchit.funda.utils;

import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

public class ViewUtils {

    public static void setRecyclerViewItemLayoutParams(View layoutView) {
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, //width
                ViewGroup.LayoutParams.WRAP_CONTENT); //height
        layoutView.setLayoutParams(lp);//override default layout params
    }

}
