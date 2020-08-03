package com.sanchit.funda.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.sanchit.funda.R;
import com.sanchit.funda.activity.PositionLineItemDetailActivity;
import com.sanchit.funda.model.MFPriceModel;
import com.sanchit.funda.model.PositionViewModel;
import com.sanchit.funda.utils.NumberUtils;
import com.sanchit.funda.utils.ViewUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class PositionsViewTableAdapter extends RecyclerView.Adapter<PositionsViewTableAdapter.ViewHolder> {

    private final List<PositionViewModel> itemList;
    private final Context context;
    private final Map<String, MFPriceModel> priceMap;

    private String grouping;

    public PositionsViewTableAdapter(Context context,
                                     List<PositionViewModel> itemList, Map<String, MFPriceModel> priceMap) {
        this.context = context;
        this.itemList = itemList;
        this.priceMap = priceMap;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.cards_positions_table, null);
        ViewUtils.setRecyclerViewItemLayoutParams(layoutView);
        ViewHolder rcv = new ViewHolder(layoutView, context, this);
        return rcv;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PositionViewModel p = itemList.get(position);

        holder.textViewSymbol.setText(p.getHead());
        holder.textViewValuation.setText(NumberUtils.formatMoney(p.getValuation()));
        holder.textViewCost.setText(NumberUtils.formatMoney(p.getInvestment()));
        holder.textViewOverallPNL.setText(NumberUtils.toPercentage(p.getPnlOverall(), 2));
        holder.textViewDayPNL.setText(NumberUtils.toPercentage(p.getPnlDay(), 2));

        BigDecimal ratio = p.getInvestment().divide(p.getTotalCost(), 4, BigDecimal.ROUND_HALF_UP);
        //BigDecimal newBarWidth = ratio.multiply(new BigDecimal(holder.barWidth));

        //holder.linearLayoutFundWeight.getLayoutParams().width = newBarWidth.intValue();
        //holder.linearLayoutFundWeight.requestLayout();
        holder.linearLayoutFundWeightText.setText(ratio.toPlainString());
        holder.setCostBarWidth();

        setValuationTone(holder.textViewOverallPNL, p.getPnlOverall());
        setValuationTone(holder.textViewDayPNL, p.getPnlDay());
    }

    private void setValuationTone(TextView view, BigDecimal value) {
        if (value.compareTo(BigDecimal.ZERO) > 0) {
            view.setTextColor(context.getResources().getColor(R.color.green_800, null));
        } else if (value.compareTo(BigDecimal.ZERO) == 0) {
            view.setTextColor(context.getResources().getColor(R.color.positions_view_text, null));
        } else {
            view.setTextColor(context.getResources().getColor(R.color.red_A700, null));
        }
    }

    @Override
    public int getItemCount() {
        return this.itemList.size();
    }

    public void setGrouping(String item) {
        this.grouping = item;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final Context context;
        private final PositionsViewTableAdapter parent;

        private final TextView textViewSymbol;
        private final TextView textViewValuation;
        private final TextView textViewCost;
        private final TextView textViewDayPNL;
        private final TextView textViewOverallPNL;

        private final LinearLayout linearLayoutFundWeight;
        private final TextView linearLayoutFundWeightText;

        private int barWidth = -1;

        public ViewHolder(View itemView, Context context, PositionsViewTableAdapter positionsViewTableAdapter) {
            super(itemView);
            itemView.setOnClickListener(this);
            this.textViewSymbol = itemView.findViewById(R.id.positions_table_symbol);
            this.textViewValuation = itemView.findViewById(R.id.positions_table_valuation);
            this.textViewCost = itemView.findViewById(R.id.positions_table_cost);
            this.textViewDayPNL = itemView.findViewById(R.id.positions_table_day_pnl);
            this.textViewOverallPNL = itemView.findViewById(R.id.positions_table_overall_pnl);
            this.linearLayoutFundWeight = itemView.findViewById(R.id.positions_table_fund_weight);
            this.linearLayoutFundWeightText = itemView.findViewById(R.id.positions_table_fund_weight_text);
            this.context = context;
            this.parent = positionsViewTableAdapter;

            ViewTreeObserver vto = linearLayoutFundWeight.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        linearLayoutFundWeight.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                        linearLayoutFundWeight.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                    barWidth = linearLayoutFundWeight.getMeasuredWidth();
                    setCostBarWidth();
                }
            });
        }

        private void setCostBarWidth() {
            if (barWidth > 0) {
                BigDecimal ratio = new BigDecimal(linearLayoutFundWeightText.getText().toString());
                BigDecimal newBarWidth = ratio.multiply(new BigDecimal(barWidth));

                linearLayoutFundWeight.getLayoutParams().width = newBarWidth.intValue();
                linearLayoutFundWeight.requestLayout();
            }
        }

        @Override
        public void onClick(View v) {
            Intent i = new Intent(context, PositionLineItemDetailActivity.class);
            TextView symbolView = v.findViewById(R.id.positions_table_symbol);
            i.putExtra("itemName", symbolView.getText());
            i.putExtra("grouping", parent.grouping);
            context.startActivity(i);
        }
    }

}
