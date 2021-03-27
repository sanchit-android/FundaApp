package com.sanchit.funda.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sanchit.funda.R;
import com.sanchit.funda.activity.MutualFundInfoActivity;
import com.sanchit.funda.activity.PositionCashflowViewActivity;
import com.sanchit.funda.cache.CacheManager;
import com.sanchit.funda.cache.Caches;
import com.sanchit.funda.model.MFDetailModel;
import com.sanchit.funda.model.MFPosition;
import com.sanchit.funda.model.MFPriceModel;
import com.sanchit.funda.model.cashflow.CashflowPosition;
import com.sanchit.funda.utils.Constants;
import com.sanchit.funda.utils.DateUtils;
import com.sanchit.funda.utils.ViewUtils;

import java.math.BigDecimal;
import java.util.List;

public class MFListingAdapter extends RecyclerView.Adapter<MFListingAdapter.ViewHolder> {

    private final Activity activity;
    private final List<MFDetailModel> itemList;

    private final CacheManager.Cache<String, MFPosition> positions;
    private final CacheManager.Cache<String, List> trades;

    public MFListingAdapter(Activity activity, List<MFDetailModel> model) {
        this.activity = activity;
        this.itemList = model;
        positions = CacheManager.get(Caches.POSITIONS, MFPosition.class);
        trades = CacheManager.get(Caches.TRADES_BY_AMFI_ID, List.class);
    }

    @NonNull
    @Override
    public MFListingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cards_fund_detail, null);
        ViewUtils.setRecyclerViewItemLayoutParams(layoutView);
        MFListingAdapter.ViewHolder rcv = new MFListingAdapter.ViewHolder(layoutView, activity);
        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull MFListingAdapter.ViewHolder holder, int position) {
        MFDetailModel p = itemList.get(position);
        holder.textViewSymbol.setText(p.getFund().getFundName());

        MFPriceModel data = p.getPriceModel();

        String amfiID = p.getFund().getAmfiID();
        holder.amfiID = amfiID;
        holder.fundName = p.getFund().getFundName();
        holder.textViewCurrent.setText(data.getPriceString(Constants.Duration.T, 2));
        holder.textViewCurrentDate.setText("(" + DateUtils.formatDate(data.getLatestNAVDate()) + ")");

        /*
        holder.textView52WHigh.setText(data.getPriceString(Constants.Duration.T_1YHigh, 2));
        holder.textView52WLow.setText(data.getPriceString(Constants.Duration.T_1YLow, 2));
        int margin = ViewUtils.getWidth(holder.textView52WLow);
        margin += marginDelta(data.getPrice(Constants.Duration.T),
                data.getPrice(Constants.Duration.T_1YLow),
                data.getPrice(Constants.Duration.T_1YHigh),
                ViewUtils.getWidth(holder.bar52wHL));
        ViewUtils.setLeftMarginOnLinearLayout(activity, holder.arrowImage, margin);
        holder.textViewCurrent.requestLayout();
        holder.arrowImage.requestLayout();
        */

        holder.textView1YPct.setText(data.get1YearReturn());
        holder.textView6MPct.setText(data.get6MonthsReturn());
        holder.textView3MPct.setText(data.get3MonthsReturn());
        holder.textView1MPct.setText(data.get1MonthReturn());

        holder.textView2YPct.setText(data.get2YearReturn());
        holder.textView3YPct.setText(data.get3YearReturn());
        holder.textView5YPct.setText(data.get5YearReturn());

        if ((positions == null || !positions.exists(amfiID)) && (trades == null || !trades.exists(amfiID))) {
            holder.textViewMyPortfolio.setVisibility(View.GONE);
        } else {
            holder.textViewMyPortfolio.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    private int marginDelta(BigDecimal p, BigDecimal l, BigDecimal h, int measuredWidth) {
        if (p == null || l == null || h == null) {
            return 0;
        }
        BigDecimal w = new BigDecimal(measuredWidth);
        BigDecimal delta = w.divide(h.subtract(l), 6, BigDecimal.ROUND_HALF_UP).multiply(p.subtract(l));
        return delta.intValue();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final Context context;
        private final TextView textViewSymbol;
        private final TextView textView52WLow;
        private final TextView textView52WHigh;
        private final TextView textViewCurrent;
        private final TextView textViewCurrentDate;
        private final LinearLayout bar52wHL;
        private final ImageView arrowImage;
        private final TextView textView5YPct;
        private final TextView textView3YPct;
        private final TextView textView2YPct;
        private final TextView textView1YPct;
        private final TextView textView6MPct;
        private final TextView textView3MPct;
        private final TextView textView1MPct;
        private final TextView textViewMyPortfolio;

        private String amfiID;
        private String fundName;

        public ViewHolder(View itemView, Context context) {
            super(itemView);
            itemView.setOnClickListener(this);
            this.textViewSymbol = itemView.findViewById(R.id.card_fund_detail_fund_name);
            this.textView52WHigh = itemView.findViewById(R.id.card_fund_detail_52whigh);
            this.textView52WLow = itemView.findViewById(R.id.card_fund_detail_52wlow);
            this.textViewCurrent = itemView.findViewById(R.id.card_fund_detail_curent_nav);
            this.textViewCurrentDate = itemView.findViewById(R.id.card_fund_detail_curent_nav_date);
            this.bar52wHL = itemView.findViewById(R.id.card_fund_detail_52hl_bar);
            this.arrowImage = itemView.findViewById(R.id.card_fund_detail_curent_nav_arrow);

            this.textView5YPct = itemView.findViewById(R.id.card_fund_detail_5Y_pct);
            this.textView3YPct = itemView.findViewById(R.id.card_fund_detail_3Y_pct);
            this.textView2YPct = itemView.findViewById(R.id.card_fund_detail_2Y_pct);
            this.textView1YPct = itemView.findViewById(R.id.card_fund_detail_1Y_pct);
            this.textView6MPct = itemView.findViewById(R.id.card_fund_detail_6M_pct);
            this.textView3MPct = itemView.findViewById(R.id.card_fund_detail_3M_pct);
            this.textView1MPct = itemView.findViewById(R.id.card_fund_detail_1M_pct);

            this.textViewMyPortfolio = itemView.findViewById(R.id.card_fund_detail_my_portfolio);

            this.context = context;

            textViewMyPortfolio.setOnClickListener(v -> {
                CacheManager.Cache<String, CashflowPosition> cashflows = CacheManager.get(Caches.CASHFLOW_POSITION_BY_AMFI_ID, CashflowPosition.class);
                List<CashflowPosition> cashflowPositions = (List<CashflowPosition>) CacheManager.get(Caches.CASHFLOW_POSITION_RAW);
                CashflowPosition cashflow = cashflows.get(amfiID);
                if (cashflow == null || cashflow.getFund() == null || cashflow.getCostPrice() == null || cashflowPositions == null || cashflowPositions.isEmpty()) {
                    return;
                }
                Intent i = new Intent(context, PositionCashflowViewActivity.class);
                i.putExtra("amfiID", amfiID);
                i.putExtra("fundName", fundName);
                context.startActivity(i);
            });
        }

        @Override
        public void onClick(View v) {
            Intent i = new Intent(this.context, MutualFundInfoActivity.class);
            i.putExtra("amfiID", amfiID);
            i.putExtra("fundName", fundName);
            this.context.startActivity(i);
        }
    }
}
