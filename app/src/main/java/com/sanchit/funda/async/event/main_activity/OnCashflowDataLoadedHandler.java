package com.sanchit.funda.async.event.main_activity;

import android.view.View;
import android.widget.TextView;

import com.sanchit.funda.MainActivity;
import com.sanchit.funda.R;
import com.sanchit.funda.adapter.HomeSummary1Adapter;
import com.sanchit.funda.adapter.HomeSummary2Adapter;
import com.sanchit.funda.async.event.OnEnrichmentCompleted;
import com.sanchit.funda.cache.CacheManager;
import com.sanchit.funda.cache.Caches;
import com.sanchit.funda.log.LogManager;
import com.sanchit.funda.model.MutualFund;
import com.sanchit.funda.model.cashflow.CashflowPosition;
import com.sanchit.funda.model.factory.DataFactory;
import com.sanchit.funda.utils.Constants;
import com.sanchit.funda.utils.NumberUtils;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OnCashflowDataLoadedHandler extends AbstractMainActivityEventHandler implements OnEnrichmentCompleted<List<CashflowPosition>> {

    public OnCashflowDataLoadedHandler(MainActivity activity) {
        super(activity);
    }

    @Override
    public void updateView(List<CashflowPosition> data) {
        CacheManager.registerRawData(Caches.CASHFLOW_POSITION_RAW, data);
        CacheManager.registerCache(Caches.CASHFLOW_POSITION_BY_AMFI_ID, toCache(data));
        fullUpdateSummary();
    }

    private CacheManager.Cache<String, CashflowPosition> toCache(List<CashflowPosition> data) {
        CacheManager.Cache<String, CashflowPosition> cache = new CacheManager.Cache<>();
        for (CashflowPosition p : data) {
            cache.add(p.getFund().getAmfiID(), p);
        }
        return cache;
    }

    private void fullUpdateSummary() {
        List<CashflowPosition> data = (List<CashflowPosition>) CacheManager.get(Caches.CASHFLOW_POSITION_RAW);

        BigDecimal valuation = BigDecimal.ZERO;
        BigDecimal investment = BigDecimal.ZERO;

        Set<String> funds = new HashSet<>();
        Set<String> categories = new HashSet<>();

        for (CashflowPosition pos : data) {
            String fundName = pos.getFund().getFundName();
            BigDecimal unrealizedQuantity = pos.getUnclosedTaxlotGroup().getUnrealizedQuantity();
            if (unrealizedQuantity != null && !NumberUtils.equals(BigDecimal.ZERO, unrealizedQuantity)) {
                MutualFund fund = pos.getFund();
                funds.add(fund.getFundName());
                categories.add(fund.getSubCategory());

                investment = investment.add(pos.getUnclosedTaxlotGroup().getCostOfUnrealizedInvestments());
                valuation = valuation.add(unrealizedQuantity.multiply(activity.priceMap.get(fund.getAmfiID()).getPrice(Constants.Duration.T)));
            }
        }

        BigDecimal pnl_abs = valuation.subtract(investment);
        BigDecimal pnl = BigDecimal.ZERO.equals(investment) ? BigDecimal.ZERO : pnl_abs.divide(investment, 4, BigDecimal.ROUND_HALF_UP);

        ((TextView) activity.findViewById(R.id.home_summary_investment)).setText(NumberUtils.formatMoney(investment));
        ((TextView) activity.findViewById(R.id.home_summary_fund_count)).setText(String.valueOf(funds.size()));
        ((TextView) activity.findViewById(R.id.home_summary_category_count)).setText(String.valueOf(categories.size()));
        ((TextView) activity.findViewById(R.id.home_summary_valuation)).setText(NumberUtils.formatMoney(valuation));
        ((TextView) activity.findViewById(R.id.home_summary_pnl)).setText(NumberUtils.toPercentage(pnl, 2));
        ((TextView) activity.findViewById(R.id.home_summary_pnl_abs)).setText(NumberUtils.formatMoney(pnl_abs));

        activity.progressBarHomeSummary.setVisibility(View.GONE);

        // Setup Summary1 section
        activity.homeSummary1Model = DataFactory.generateHomeSummary1Model(activity.positions, activity.trades);
        activity.homeSummary1Adapter = new HomeSummary1Adapter(activity, activity.homeSummary1Model);
        activity.recyclerViewHomeSummary1.setAdapter(activity.homeSummary1Adapter);

        // Setup Summary2 section
        activity.homeSummary2Model = DataFactory.generateHomeSummary2Model(activity.positions, activity.trades);
        activity.homeSummary2Adapter = new HomeSummary2Adapter(activity, activity.homeSummary2Model);
        activity.recyclerViewHomeSummary2.setAdapter(activity.homeSummary2Adapter);

        long endTime = System.currentTimeMillis();
        long durationMilis = endTime - activity.startTime;
        LogManager.log("Completed loading home in " + durationMilis + " msec.");
    }
}