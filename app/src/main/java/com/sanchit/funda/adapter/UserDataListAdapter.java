package com.sanchit.funda.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sanchit.funda.R;
import com.sanchit.funda.dao.entity.UserDataModel;
import com.sanchit.funda.utils.ViewUtils;

import java.util.List;

public class UserDataListAdapter extends RecyclerView.Adapter<UserDataListAdapter.ViewHolder> {

    private final Activity activity;
    private final List<UserDataModel> itemList;

    public UserDataListAdapter(Activity activity, List<UserDataModel> model) {
        this.activity = activity;
        this.itemList = model;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cards_user_data_entries, null);
        ViewUtils.setRecyclerViewItemLayoutParams(layoutView);
        UserDataListAdapter.ViewHolder rcv = new UserDataListAdapter.ViewHolder(layoutView, activity);
        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserDataModel p = itemList.get(position);
        holder.textViewName.setText(p.name);
        holder.textViewPAN.setText(p.pan.substring(0, 3) + "*");

        String fileName = p.uri.substring(p.uri.lastIndexOf("/") + 1);
        holder.textViewFile.setText(fileName);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView textViewName;
        private final TextView textViewPAN;
        private final TextView textViewFile;
        private final Context context;

        public ViewHolder(View itemView, Context context) {
            super(itemView);
            itemView.setOnClickListener(this);
            this.textViewName = itemView.findViewById(R.id.card_text_view_user_detail_name);
            this.textViewPAN = itemView.findViewById(R.id.card_text_view_user_detail_pan);
            this.textViewFile = itemView.findViewById(R.id.card_text_view_user_detail_file);

            this.context = context;
        }

        @Override
        public void onClick(View v) {
        }
    }
}
