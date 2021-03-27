package com.sanchit.funda;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sanchit.funda.activity.LoggingActivity;
import com.sanchit.funda.activity.MFTradeEntryActivity;
import com.sanchit.funda.activity.PositionsViewActivity;
import com.sanchit.funda.activity.UserDataCaptureActivity;
import com.sanchit.funda.adapter.HomeSummary1Adapter;
import com.sanchit.funda.adapter.HomeSummary2Adapter;
import com.sanchit.funda.adapter.HomeSummaryTopFundsAdapter;
import com.sanchit.funda.async.CashflowAsyncLoader;
import com.sanchit.funda.async.FundsRawDataAsyncLoader;
import com.sanchit.funda.async.GrowwStatementAsyncLoader;
import com.sanchit.funda.async.MFAPI_NAVAsyncLoader;
import com.sanchit.funda.async.NSDL_CASAsyncLoader;
import com.sanchit.funda.async.event.OnEnrichmentCompleted;
import com.sanchit.funda.cache.CacheManager;
import com.sanchit.funda.cache.Caches;
import com.sanchit.funda.model.MFDetailModel;
import com.sanchit.funda.model.MFPosition;
import com.sanchit.funda.model.MFPriceModel;
import com.sanchit.funda.model.MFTrade;
import com.sanchit.funda.model.MutualFund;
import com.sanchit.funda.model.cashflow.CashflowPosition;
import com.sanchit.funda.model.factory.DataFactory;
import com.sanchit.funda.model.factory.MFDetailModelFactory;
import com.sanchit.funda.model.homesummary.AbstractHomeSummary1Model;
import com.sanchit.funda.model.homesummary.AbstractHomeSummary2Model;
import com.sanchit.funda.model.homesummary.TopFundModel;
import com.sanchit.funda.utils.Constants;
import com.sanchit.funda.utils.NumberUtils;
import com.sanchit.funda.utils.SecurityUtils;
import com.sanchit.funda.utils.ViewUtils;
import com.tom_roush.pdfbox.util.PDFBoxResourceLoader;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public static final String LARGE_CAP_FUND = "Large Cap Fund";

    private List<MFPosition> positions = new ArrayList<>();
    private Map<String, MFPriceModel> priceMap = new HashMap<>();
    private List<MFTrade> trades = new ArrayList<>();
    private List<MFDetailModel> mfDetailModel = new ArrayList<>();

    private RecyclerView recyclerViewHomeSummary1;
    private List<AbstractHomeSummary1Model> homeSummary1Model;
    private HomeSummary1Adapter homeSummary1Adapter;

    private RecyclerView recyclerViewHomeSummary2;
    private List<AbstractHomeSummary2Model> homeSummary2Model;
    private HomeSummary2Adapter homeSummary2Adapter;

    private ProgressBar progressBarHomeSummary;
    private ProgressBar progressBarTopFunds;

    private Integer priceRequestsPending = 0;

    private Uri ecasFilePath;
    private String PAN;
    private String name;
    private String fileType;

    private Spinner spinnerFundCategories;
    private RecyclerView recylerViewTopFunds;
    private List<TopFundModel> topFundsModel;
    private HomeSummaryTopFundsAdapter homeSummaryTopFundsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ecasFilePath = (Uri) getIntent().getParcelableExtra("uri");
        PAN = getIntent().getStringExtra("PAN");
        name = getIntent().getStringExtra("name");
        fileType = getIntent().getStringExtra("fileType");

        setTitle("Hi " + name + "!");
        getSupportActionBar().setElevation(0);
        ViewUtils.setActionBarColor(this, R.color.colorPrimaryDark);
        setTitleColor(getResources().getColor(R.color.colorTextLight));
        setContentView(R.layout.activity_main);

        SecurityUtils.setupPermissions(this);

        positions = new ArrayList<>();
        priceMap = new HashMap<>();
        trades = new ArrayList<>();
        mfDetailModel = new ArrayList<>();

        progressBarHomeSummary = findViewById(R.id.progressBar_home_summary);
        progressBarTopFunds = findViewById(R.id.progressBar_home_summary_top_funds);
        progressBarHomeSummary.setVisibility(View.VISIBLE);
        progressBarTopFunds.setVisibility(View.VISIBLE);

        PDFBoxResourceLoader.init(getApplicationContext());
        new FundsRawDataAsyncLoader(MainActivity.this, new FundsRawDataLoadedHandler(MainActivity.this)).execute(Uri.EMPTY);

        recyclerViewHomeSummary1 = findViewById(R.id.recycler_view_home_summary_1);
        recyclerViewHomeSummary1.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        recyclerViewHomeSummary2 = findViewById(R.id.recycler_view_home_summary_2);
        recyclerViewHomeSummary2.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        recylerViewTopFunds = findViewById(R.id.recycler_view_top_funds_home);
        recylerViewTopFunds.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        setupStaticData();
    }

    private void setupStaticData() {
        spinnerFundCategories = findViewById(R.id.spinner_fund_categories_home_summary);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.fund_categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFundCategories.setAdapter(adapter);
        spinnerFundCategories.setOnItemSelectedListener(this);

        topFundsModel = TopFundModel.blankModel(3);
        homeSummaryTopFundsAdapter = new HomeSummaryTopFundsAdapter(MainActivity.this, topFundsModel);
        recylerViewTopFunds.setAdapter(homeSummaryTopFundsAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_main_activity_reload_ecas:
                Intent i = new Intent(this, UserDataCaptureActivity.class);
                startActivity(i);
                return true;
            case R.id.menu_main_activity_logs:
                i = new Intent(this, LoggingActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        MFTrade trade = (MFTrade) intent.getSerializableExtra("trade");
        if (trade != null) {
            ((List<MFTrade>) CacheManager.get(Caches.TRADES)).add(trade);
            CacheManager.getOrRegisterCache(Caches.TRADES_BY_AMFI_ID, List.class).get(trade.getFund().getAmfiID()).add(trade);

            ++priceRequestsPending;
            new MFAPI_NAVAsyncLoader(this, new OnMFAPI_PriceLoadedHandler()).execute(trade.getFund().getAmfiID());
            new CashflowAsyncLoader(this, new OnCashflowDataLoadedHandler(), trades).execute();
        } else {
            Uri newEcasFilePath = (Uri) intent.getParcelableExtra("uri");
            String newPAN = intent.getStringExtra("PAN");
            String newFileType = intent.getStringExtra("fileType");

            if (!ecasFilePath.equals(newEcasFilePath) || !newPAN.equals(PAN) || !fileType.equals(newFileType)) {
                PAN = newPAN;
                ecasFilePath = newEcasFilePath;
                fileType = newFileType;
                progressBarHomeSummary.setVisibility(View.VISIBLE);

                positions = new ArrayList<>();
                priceMap = new HashMap<>();
                trades = new ArrayList<>();
                mfDetailModel = new ArrayList<>();

                new FundsRawDataAsyncLoader(this, new FundsRawDataLoadedHandler(this)).execute(Uri.EMPTY);
            }
        }
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
                valuation = valuation.add(unrealizedQuantity.multiply(priceMap.get(fund.getAmfiID()).getPrice(Constants.Duration.T)));
            }
        }

        BigDecimal pnl_abs = valuation.subtract(investment);
        BigDecimal pnl = BigDecimal.ZERO.equals(investment) ? BigDecimal.ZERO : pnl_abs.divide(investment, 4, BigDecimal.ROUND_HALF_UP);

        ((TextView) findViewById(R.id.home_summary_investment)).setText(NumberUtils.formatMoney(investment));
        ((TextView) findViewById(R.id.home_summary_fund_count)).setText(String.valueOf(funds.size()));
        ((TextView) findViewById(R.id.home_summary_category_count)).setText(String.valueOf(categories.size()));
        ((TextView) findViewById(R.id.home_summary_valuation)).setText(NumberUtils.formatMoney(valuation));
        ((TextView) findViewById(R.id.home_summary_pnl)).setText(NumberUtils.toPercentage(pnl, 2));
        ((TextView) findViewById(R.id.home_summary_pnl_abs)).setText(NumberUtils.formatMoney(pnl_abs));

        progressBarHomeSummary.setVisibility(View.GONE);

        // Setup Summary1 section
        homeSummary1Model = DataFactory.generateHomeSummary1Model(positions, trades);
        homeSummary1Adapter = new HomeSummary1Adapter(this, homeSummary1Model);
        recyclerViewHomeSummary1.setAdapter(homeSummary1Adapter);

        // Setup Summary2 section
        homeSummary2Model = DataFactory.generateHomeSummary2Model(positions, trades);
        homeSummary2Adapter = new HomeSummary2Adapter(this, homeSummary2Model);
        recyclerViewHomeSummary2.setAdapter(homeSummary2Adapter);
    }

    public void onClickHomeSummary(View view) {
        Intent i = new Intent(this, PositionsViewActivity.class);
        i.putExtra("positions", (ArrayList) positions);
        i.putExtra("trades", (ArrayList) trades);
        i.putExtra("prices", (HashMap) priceMap);
        startActivity(i);
    }

    public void onClickHomeTradeEntry(View view) {
        Intent i = new Intent(this, MFTradeEntryActivity.class);
        startActivity(i);
    }

    private CacheManager.Cache<String, MutualFund> generateCacheByName(List<MutualFund> data) {
        CacheManager.Cache<String, MutualFund> cache = new CacheManager.Cache<>();
        for (MutualFund fund : data) {
            cache.add(fund.getFundName().toLowerCase(), fund);
        }
        return cache;
    }

    private CacheManager.Cache<String, MutualFund> generateCacheByAmfiId(List<MutualFund> data) {
        CacheManager.Cache<String, MutualFund> cache = new CacheManager.Cache<>();
        for (MutualFund fund : data) {
            cache.add(fund.getAmfiID(), fund);
        }
        return cache;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
        if (CacheManager.get(Caches.FUNDS) != null) {
            homeSummaryTopFundsAdapter.setGrouping(item);
            new MFDetailModelFactory(item, MFDetailModelFactory.Criteria.FundAppDefCategoryCriteria, new OnMFDetailModelLoadedHandler());
            progressBarTopFunds.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private void initiatePriceUpdateRequests() {
        Set<String> amfiIDs = new HashSet<>();
        for (MFPosition position : positions) {
            String amfiID = position.getFund().getAmfiID();
            if (amfiIDs.contains(amfiID)) {
                continue;
            }

            new MFAPI_NAVAsyncLoader(this, new OnMFAPI_PriceLoadedHandler()).execute(amfiID);
            ++priceRequestsPending;
            amfiIDs.add(amfiID);
        }
        for (MFTrade trade : trades) {
            String amfiID = trade.getFund().getAmfiID();
            if (amfiIDs.contains(amfiID)) {
                continue;
            }

            new MFAPI_NAVAsyncLoader(this, new OnMFAPI_PriceLoadedHandler()).execute(amfiID);
            ++priceRequestsPending;
            amfiIDs.add(amfiID);
        }
    }

    private class OnECASFileLoadedHandler implements OnEnrichmentCompleted<List<MFPosition>> {
        @Override
        public void updateView(List<MFPosition> data) {
            if (data != null) {
                positions.addAll(data);
                CacheManager.registerCache(Caches.POSITIONS, toCache(positions));
                initiatePriceUpdateRequests();
            }
        }

        private CacheManager.Cache<String, MFPosition> toCache(List<MFPosition> data) {
            CacheManager.Cache<String, MFPosition> cache = new CacheManager.Cache<>();
            for (MFPosition pos : data) {
                cache.add(pos.getFund().getAmfiID(), pos);
            }
            return cache;
        }
    }

    private class OnGrowwStatementLoadedHandler implements OnEnrichmentCompleted<List<MFTrade>> {
        @Override
        public void updateView(List<MFTrade> data) {
            if (data != null) {
                trades.addAll(data);
                CacheManager.registerRawData(Caches.TRADES, trades);
                CacheManager.registerCache(Caches.TRADES_BY_AMFI_ID, toCache(trades));
                initiatePriceUpdateRequests();
            }
        }

        private CacheManager.Cache<String, List<MFTrade>> toCache(List<MFTrade> trades) {
            CacheManager.Cache<String, List<MFTrade>> cache = new CacheManager.Cache<>();
            for (MFTrade t : trades) {
                if (!cache.exists(t.getFund().getAmfiID())) {
                    cache.add(t.getFund().getAmfiID(), new ArrayList<>());
                }
                cache.get(t.getFund().getAmfiID()).add(t);
            }
            return cache;
        }
    }

    private class FundsRawDataLoadedHandler implements OnEnrichmentCompleted<List<MutualFund>> {

        private final Activity activity;

        public FundsRawDataLoadedHandler(Activity activity) {
            this.activity = activity;
        }

        @Override
        public void updateView(List<MutualFund> data) {
            CacheManager.registerRawData(Caches.FUNDS, data);
            CacheManager.registerCache(Caches.FUNDS_BY_NAME, generateCacheByName(data));
            CacheManager.registerCache(Caches.FUNDS_BY_AMFI_ID, generateCacheByAmfiId(data));

            if ("Groww.in Transaction Statement".equals(fileType)) {
                new GrowwStatementAsyncLoader(MainActivity.this, new OnGrowwStatementLoadedHandler(), PAN).execute(ecasFilePath);
            } else {
                new NSDL_CASAsyncLoader(MainActivity.this, new OnECASFileLoadedHandler(), PAN).execute(ecasFilePath);
            }

            homeSummaryTopFundsAdapter.setGrouping(LARGE_CAP_FUND);
            new MFDetailModelFactory(LARGE_CAP_FUND, MFDetailModelFactory.Criteria.FundAppDefCategoryCriteria, new OnMFDetailModelLoadedHandler());
        }
    }

    private class OnMFAPI_PriceLoadedHandler implements OnEnrichmentCompleted<MFPriceModel> {
        @Override
        public void updateView(MFPriceModel data) {
            priceMap.put(data.getAmfiID(), data);
            --priceRequestsPending;

            CacheManager.getOrRegisterCache(Caches.PRICES_BY_AMFI_ID, MFPriceModel.class).add(data.getAmfiID(), data);

            if (priceRequestsPending == 0) {
                new CashflowAsyncLoader(MainActivity.this, new OnCashflowDataLoadedHandler(), trades).execute();
            }
        }
    }

    private class OnCashflowDataLoadedHandler implements OnEnrichmentCompleted<List<CashflowPosition>> {

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
    }

    private class OnMFDetailModelLoadedHandler implements MFDetailModelFactory.OnCompletionHandler {

        @Override
        public void onMFDetailDatasetLoaded(List<MFDetailModel> mfDetailModelList) {
            mfDetailModel.clear();
            mfDetailModel.addAll(mfDetailModelList);
            Collections.sort(mfDetailModel, (o1, o2) -> o1.getPriceModel().get1YearReturnComparable().compareTo(o2.getPriceModel().get1YearReturnComparable()) * -1);

            if (topFundsModel == null) {
                topFundsModel = DataFactory.generateHomeSummaryTopFundsModel(mfDetailModel);
                homeSummaryTopFundsAdapter = new HomeSummaryTopFundsAdapter(MainActivity.this, topFundsModel);
                recylerViewTopFunds.setAdapter(homeSummaryTopFundsAdapter);
            } else {
                topFundsModel.clear();
                topFundsModel.addAll(DataFactory.generateHomeSummaryTopFundsModel(mfDetailModel));
                homeSummaryTopFundsAdapter.notifyDataSetChanged();
            }

            progressBarTopFunds.setVisibility(View.INVISIBLE);
        }
    }
}
