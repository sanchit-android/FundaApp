package com.sanchit.funda.activity;

import android.os.Bundle;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sanchit.funda.R;
import com.sanchit.funda.adapter.CashflowTradeListAdapter;
import com.sanchit.funda.cache.CacheManager;
import com.sanchit.funda.cache.Caches;
import com.sanchit.funda.model.MFPriceModel;
import com.sanchit.funda.model.cashflow.CashflowPosition;
import com.sanchit.funda.model.cashflow.ClosedTaxlotGroup;
import com.sanchit.funda.model.cashflow.Taxlot;
import com.sanchit.funda.utils.Constants;
import com.sanchit.funda.utils.NumberUtils;
import com.sanchit.funda.utils.ViewUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PositionCashflowViewActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    public static final Comparator<Taxlot> TAXLOT_COMPARATOR = (o1, o2) -> {
        // sort by investment date & for a buy/sell on same date, put sell first then buy
        if (!o2.getInvestmentDate().equals(o1.getInvestmentDate())) {
            return o2.getInvestmentDate().compareTo(o1.getInvestmentDate());
        }
        return o2.getSide().compareTo(o1.getSide());
    };

    private String amfiId;
    private CacheManager.Cache<String, CashflowPosition> cashflows;
    private CacheManager.Cache<String, MFPriceModel> prices;

    private RecyclerView recyclerViewTrades;
    private CashflowTradeListAdapter tradesListAdapter;
    private List<Taxlot> tradeList;
    private Switch switchRealizedTrades;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        getSupportActionBar().setElevation(0);
        ViewUtils.setActionBarColor(this, R.color.colorPrimaryDark);
        setContentView(R.layout.activity_position_cashflow_view);

        amfiId = getIntent().getStringExtra("amfiID");
        cashflows = CacheManager.get(Caches.CASHFLOW_POSITION_BY_AMFI_ID, CashflowPosition.class);
        prices = CacheManager.get(Caches.PRICES_BY_AMFI_ID, MFPriceModel.class);

        recyclerViewTrades = findViewById(R.id.recycler_view_cashflow_trade_list);
        recyclerViewTrades.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        injectDataIntoViews();

        switchRealizedTrades = findViewById(R.id.switch_realized_positions);
        switchRealizedTrades.setOnCheckedChangeListener(this);
    }

    private void injectDataIntoViews() {
        CashflowPosition cashflow = cashflows.get(amfiId);
        MFPriceModel priceModel = prices.get(amfiId);

        BigDecimal currentNAV = priceModel.getPrice(Constants.Duration.T);
        BigDecimal investment = cashflow.getUnclosedTaxlotGroup().getCostOfUnrealizedInvestments();
        BigDecimal valuation = cashflow.getUnclosedTaxlotGroup().getUnrealizedQuantity().multiply(currentNAV);
        BigDecimal pnl = valuation.subtract(investment);

        ViewUtils.setTextViewData(this, R.id.mf_detail_fund_name, cashflow.getFund().getFundName());
        ViewUtils.setTextViewData(this, R.id.pos_cf_invested, NumberUtils.formatMoney(investment));
        ViewUtils.setTextViewData(this, R.id.pos_cf_valuation, NumberUtils.formatMoney(valuation));
        ViewUtils.setTextViewData(this, R.id.pos_cf_pnl, NumberUtils.formatMoney(pnl));
        ViewUtils.setTextViewData(this, R.id.pos_cf_quantity, cashflow.getUnclosedTaxlotGroup().getUnrealizedQuantity().toPlainString());
        ViewUtils.setTextViewData(this, R.id.pos_cf_cost_price, NumberUtils.formatMoney(cashflow.getCostPrice()));
        ViewUtils.setTextViewData(this, R.id.pos_cf_current_nav, NumberUtils.formatMoney(currentNAV));
        ViewUtils.setTextViewData(this, R.id.pos_cf_realized_pnl, NumberUtils.formatMoney(cashflow.getTotalRealizedPNL()));

        tradeList = new ArrayList<>(cashflow.getUnclosedTaxlotGroup().getUnrealizedTaxlots());
        //Collections.reverse(tradeList);
        tradesListAdapter = new CashflowTradeListAdapter(this, tradeList);
        recyclerViewTrades.setAdapter(tradesListAdapter);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        CashflowPosition cashflow = cashflows.get(amfiId);
        tradeList.clear();
        tradeList.addAll(cashflow.getUnclosedTaxlotGroup().getUnrealizedTaxlots());
        if (isChecked) {
            for (ClosedTaxlotGroup t : cashflow.getRealizedTaxlots()) {
                tradeList.addAll(t.getOpeningLots());
                tradeList.addAll(t.getClosingLots());
            }
        } else {
            // The toggle is disabled
        }
        Collections.sort(tradeList, TAXLOT_COMPARATOR);
        tradesListAdapter.notifyDataSetChanged();
    }
}