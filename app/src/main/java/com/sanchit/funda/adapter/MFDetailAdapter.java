package com.sanchit.funda.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sanchit.funda.R;
import com.sanchit.funda.async.MFAPI_NAVAsyncLoader;
import com.sanchit.funda.async.event.OnEnrichmentCompleted;
import com.sanchit.funda.model.MFDetailModel;
import com.sanchit.funda.model.MFPriceModel;
import com.sanchit.funda.utils.Constants;
import com.sanchit.funda.utils.ViewUtils;

import java.math.BigDecimal;
import java.util.List;

public class MFDetailAdapter extends RecyclerView.Adapter<MFDetailAdapter.ViewHolder> {

    private final Activity activity;
    private final List<MFDetailModel> itemList;

    public MFDetailAdapter(Activity activity, List<MFDetailModel> model) {
        this.activity = activity;
        this.itemList = model;
    }

    @NonNull
    @Override
    public MFDetailAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cards_fund_detail, null);
        ViewUtils.setRecyclerViewItemLayoutParams(layoutView);
        MFDetailAdapter.ViewHolder rcv = new MFDetailAdapter.ViewHolder(layoutView, activity);
        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull MFDetailAdapter.ViewHolder holder, int position) {
        MFDetailModel p = itemList.get(position);
        holder.textViewSymbol.setText(p.getFund().getFundName());

        new MFAPI_NAVAsyncLoader(activity, new OnMFAPI_PriceLoadedHandler(activity, p, holder), MFAPI_NAVAsyncLoader.EnrichmentModel.HL_52W)
                .execute(p.getFund().getAmfiID());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final Context context;
        private final TextView textViewSymbol;
        private final TextView textView52WLow;
        private final TextView textView52WHigh;
        private final TextView textViewCurrent;
        private final LinearLayout bar52wHL;
        private final ImageView arrowImage;

        public ViewHolder(View itemView, Context context) {
            super(itemView);
            itemView.setOnClickListener(this);
            this.textViewSymbol = itemView.findViewById(R.id.card_fund_detail_fund_name);
            this.textView52WHigh = itemView.findViewById(R.id.card_fund_detail_52whigh);
            this.textView52WLow = itemView.findViewById(R.id.card_fund_detail_52wlow);
            this.textViewCurrent = itemView.findViewById(R.id.card_fund_detail_curent_nav);
            this.bar52wHL = itemView.findViewById(R.id.card_fund_detail_52hl_bar);
            this.arrowImage = itemView.findViewById(R.id.card_fund_detail_curent_nav_arrow);
            this.context = context;
        }

        @Override
        public void onClick(View v) {
        }
    }

    private static class OnMFAPI_PriceLoadedHandler implements OnEnrichmentCompleted<MFPriceModel> {
        private final MFDetailModel parentModel;
        private final ViewHolder viewModel;
        private final Context context;

        public OnMFAPI_PriceLoadedHandler(Context context, MFDetailModel p, ViewHolder holder) {
            this.context = context;
            this.parentModel = p;
            this.viewModel = holder;
        }

        @Override
        public void updateView(MFPriceModel data) {
            this.viewModel.textView52WHigh.setText(data.getPriceString(Constants.Duration.High52W, 2));
            this.viewModel.textView52WLow.setText(data.getPriceString(Constants.Duration.Low52W, 2));
            this.viewModel.textViewCurrent.setText(data.getPriceString(Constants.Duration.T, 2));

            int margin = this.viewModel.textView52WLow.getMeasuredWidth();
            margin += marginDelta(data.getPrice(Constants.Duration.T),
                    data.getPrice(Constants.Duration.Low52W),
                    data.getPrice(Constants.Duration.High52W),
                    this.viewModel.bar52wHL.getMeasuredWidth());

            ViewUtils.setLeftMarginOnConstrainLayout(context, this.viewModel.textViewCurrent, margin);
            ViewUtils.setLeftMarginOnConstrainLayout(context, this.viewModel.arrowImage, margin);
        }

        private int marginDelta(BigDecimal p, BigDecimal l, BigDecimal h, int measuredWidth) {
            BigDecimal w = new BigDecimal(measuredWidth);

            BigDecimal delta = w.divide(h.subtract(l), 4, BigDecimal.ROUND_HALF_UP).multiply(p.subtract(l));
            return delta.intValue();
        }
    }
}
