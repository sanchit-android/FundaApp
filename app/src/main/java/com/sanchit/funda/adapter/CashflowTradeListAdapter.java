package com.sanchit.funda.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sanchit.funda.R;
import com.sanchit.funda.activity.MFTradeEntryActivity;
import com.sanchit.funda.model.cashflow.Taxlot;
import com.sanchit.funda.utils.DateUtils;
import com.sanchit.funda.utils.NumberUtils;
import com.sanchit.funda.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

public class CashflowTradeListAdapter extends RecyclerView.Adapter<CashflowTradeListAdapter.ViewHolder> {

    private final List<Taxlot> itemList;
    private final Context context;

    public CashflowTradeListAdapter(Context context,
                                    List<Taxlot> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.cards_cashflow_trade_info_2, null);
        ViewUtils.setRecyclerViewItemLayoutParams(layoutView);
        ViewHolder rcv = new ViewHolder(layoutView, context);
        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Taxlot t = itemList.get(position);
        holder.textViewCost.setText(NumberUtils.formatMoney(t.getCost()));
        holder.textViewCostPrice.setText(NumberUtils.formatMoney(t.getCostPrice()));
        holder.textViewDate.setText(DateUtils.formatDate(t.getInvestmentDate()));
        holder.textViewQuantity.setText(t.getQuantity().toPlainString());
        holder.textViewSide.setText(t.getSide());
        setTone(holder.textViewSide, t.getSide());
        holder.taxlot = t;
    }

    private void setTone(TextView view, String value) {
        if ("Buy".equals(value)) {
            view.setTextColor(context.getResources().getColor(R.color.green_800, null));
        } else if ("Sell".equals(value)) {
            view.setTextColor(context.getResources().getColor(R.color.red_A700, null));
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView textViewCost;
        private final TextView textViewCostPrice;
        private final TextView textViewDate;
        private final TextView textViewQuantity;
        private final TextView textViewSide;
        private final Context context;

        private Taxlot taxlot;

        public ViewHolder(View itemView, Context context) {
            super(itemView);
            itemView.setOnClickListener(this);
            this.textViewCost = itemView.findViewById(R.id.cashflow_trade_cost);
            this.textViewCostPrice = itemView.findViewById(R.id.cashflow_trade_cost_price);
            this.textViewDate = itemView.findViewById(R.id.cashflow_trade_date);
            this.textViewQuantity = itemView.findViewById(R.id.cashflow_trade_quantity);
            this.textViewSide = itemView.findViewById(R.id.cashflow_trade_side);
            this.context = context;
        }

        @Override
        public void onClick(View v) {
            Intent i = new Intent(context, MFTradeEntryActivity.class);
            i.putExtra("taxlot", taxlot);
            context.startActivity(i);
        }
    }
}
