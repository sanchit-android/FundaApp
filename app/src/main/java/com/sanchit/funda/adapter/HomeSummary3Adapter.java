package com.sanchit.funda.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.sanchit.funda.R;
import com.sanchit.funda.model.HomeSummary3Model;

import java.util.List;

public class HomeSummary3Adapter extends RecyclerView.Adapter<HomeSummary3Adapter.ViewHolder> {

    private final List<HomeSummary3Model> itemList;
    private final Context context;

    public HomeSummary3Adapter(Context context,
                               List<HomeSummary3Model> itemList) {
        this.context = context;
        this.itemList = itemList;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.cards_home_summary_3, null);
        ViewHolder rcv = new ViewHolder(layoutView, context);
        return rcv;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.textViewIndex.setText(itemList.get(position).getIndex());
        holder.textViewPrice.setText(itemList.get(position).getPriceString());
        holder.textViewChange.setText(itemList.get(position).getChangeString());
    }

    @Override
    public int getItemCount() {
        return this.itemList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final CardView card;
        private final TextView textViewIndex;
        private final TextView textViewPrice;
        private final TextView textViewChange;
        private final Context context;

        public ViewHolder(View itemView, Context context) {
            super(itemView);
            itemView.setOnClickListener(this);
            this.textViewIndex = itemView.findViewById(R.id.home_summary_3_index);
            this.textViewPrice = itemView.findViewById(R.id.home_summary_3_price);
            this.textViewChange = itemView.findViewById(R.id.home_summary_3_change);
            this.card = itemView.findViewById(R.id.home_summary_3_card_view);
            this.context = context;
        }

        @Override
        public void onClick(View v) {
        }
    }
}

