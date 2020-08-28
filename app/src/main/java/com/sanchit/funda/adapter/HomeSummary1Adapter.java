package com.sanchit.funda.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.sanchit.funda.R;
import com.sanchit.funda.model.homesummary.AbstractHomeSummary1Model;

import java.util.List;

public class HomeSummary1Adapter extends RecyclerView.Adapter<HomeSummary1Adapter.InvestmentSynopsis2ViewHolder> {

    private final List<AbstractHomeSummary1Model> itemList;
    private final Context context;

    public HomeSummary1Adapter(Context context,
                               List<AbstractHomeSummary1Model> itemList) {
        this.context = context;
        this.itemList = itemList;
    }


    @Override
    public InvestmentSynopsis2ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.cards_home_summary_1, null);
        InvestmentSynopsis2ViewHolder rcv = new InvestmentSynopsis2ViewHolder(layoutView, context);
        return rcv;
    }

    @Override
    public void onBindViewHolder(InvestmentSynopsis2ViewHolder holder, int position) {
        holder.textViewBoxHeader.setText(itemList.get(position).getHeader());
        holder.textViewBoxContent.setText(itemList.get(position).getContent());
    }

    @Override
    public int getItemCount() {
        return this.itemList.size();
    }


    public static class InvestmentSynopsis2ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final CardView card;
        private final TextView textViewBoxHeader;
        private final TextView textViewBoxContent;
        private final Context context;

        public InvestmentSynopsis2ViewHolder(View itemView, Context context) {
            super(itemView);
            itemView.setOnClickListener(this);
            this.textViewBoxHeader = itemView.findViewById(R.id.home_summary_1_header);
            this.textViewBoxContent = itemView.findViewById(R.id.home_summary_1_content);
            this.card = itemView.findViewById(R.id.home_summary_1_card_view);
            this.context = context;
        }

        @Override
        public void onClick(View v) {
        }
    }
}

