package com.sanchit.funda.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.sanchit.funda.R;
import com.sanchit.funda.model.MFPosition;
import com.sanchit.funda.utils.NumberUtils;
import com.sanchit.funda.utils.ViewUtils;

import java.util.List;

public class PositionsViewTableAdapter extends RecyclerView.Adapter<PositionsViewTableAdapter.ViewHolder> {

    private final List<MFPosition> itemList;
    private final Context context;

    public PositionsViewTableAdapter(Context context,
                                     List<MFPosition> itemList) {
        this.context = context;
        this.itemList = itemList;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.cards_positions_table, null);
        ViewUtils.setRecyclerViewItemLayoutParams(layoutView);
        ViewHolder rcv = new ViewHolder(layoutView, context);
        return rcv;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.textViewSymbol.setText(itemList.get(position).getFund().getFundName());
        holder.textViewValuation.setText(NumberUtils.formatMoney(itemList.get(position).getCurrentValue()));
        holder.textViewCost.setText(NumberUtils.formatMoney(itemList.get(position).getCost()));
    }

    @Override
    public int getItemCount() {
        return this.itemList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final Context context;
        private final TextView textViewSymbol;
        private final TextView textViewValuation;
        private final TextView textViewCost;
        private final TextView textViewDayPNL;
        private final TextView textViewOverallPNL;

        public ViewHolder(View itemView, Context context) {
            super(itemView);
            itemView.setOnClickListener(this);
            this.textViewSymbol = itemView.findViewById(R.id.positions_table_symbol);
            this.textViewValuation = itemView.findViewById(R.id.positions_table_valuation);
            this.textViewCost = itemView.findViewById(R.id.positions_table_cost);
            this.textViewDayPNL = itemView.findViewById(R.id.positions_table_day_pnl);
            this.textViewOverallPNL = itemView.findViewById(R.id.positions_table_overall_pnl);
            this.context = context;
        }

        @Override
        public void onClick(View v) {
        }
    }

}
