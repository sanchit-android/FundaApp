package com.sanchit.funda.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.sanchit.funda.R;
import com.sanchit.funda.activity.PositionLineItemDetailActivity;
import com.sanchit.funda.model.homesummary.TopFundModel;
import com.sanchit.funda.utils.ViewUtils;

import java.util.List;

public class HomeSummaryTopFundsAdapter extends RecyclerView.Adapter<HomeSummaryTopFundsAdapter.ViewHolder> {

    private final List<TopFundModel> itemList;
    private final Context context;
    private String grouping;

    public HomeSummaryTopFundsAdapter(Context context,
                                      List<TopFundModel> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.cards_home_summary_top_fund, null);
        ViewUtils.setRecyclerViewItemLayoutParams(layoutView);
        ViewHolder rcv = new ViewHolder(layoutView, context);
        return rcv;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TopFundModel model = itemList.get(position);
        holder.textViewFundName.setText(model.getFundName());
        holder.textViewFundReturn.setText(model.getReturns());
    }

    @Override
    public int getItemCount() {
        return this.itemList.size();
    }

    public void setGrouping(String grouping) {
        this.grouping = grouping;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView textViewFundName;
        private final TextView textViewFundReturn;
        private final Context context;

        public ViewHolder(View itemView, Context context) {
            super(itemView);
            itemView.setOnClickListener(this);
            this.textViewFundName = itemView.findViewById(R.id.home_summary_top_fund_name);
            this.textViewFundReturn = itemView.findViewById(R.id.home_summary_top_fund_return);
            this.context = context;
        }

        @Override
        public void onClick(View v) {
            Intent i = new Intent(context, PositionLineItemDetailActivity.class);
            i.putExtra("itemName", grouping);
            i.putExtra("grouping", context.getResources().getStringArray(R.array.positions_view_grouping)[3]);
            context.startActivity(i);
        }
    }
}
