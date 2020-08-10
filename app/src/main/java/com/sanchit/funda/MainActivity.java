package com.sanchit.funda;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sanchit.funda.activity.MFTradeEntryActivity;
import com.sanchit.funda.activity.PositionsViewActivity;
import com.sanchit.funda.activity.UserDataCaptureActivity;
import com.sanchit.funda.adapter.HomeSummary1Adapter;
import com.sanchit.funda.adapter.HomeSummary2Adapter;
import com.sanchit.funda.async.FundsRawDataAsyncLoader;
import com.sanchit.funda.async.MFAPI_NAVAsyncLoader;
import com.sanchit.funda.async.NSDL_CASAsyncLoader;
import com.sanchit.funda.async.event.OnEnrichmentCompleted;
import com.sanchit.funda.cache.CacheManager;
import com.sanchit.funda.cache.Caches;
import com.sanchit.funda.model.HomeSummary1Model;
import com.sanchit.funda.model.HomeSummary2Model;
import com.sanchit.funda.model.MFPosition;
import com.sanchit.funda.model.MFPriceModel;
import com.sanchit.funda.model.MFTrade;
import com.sanchit.funda.model.MutualFund;
import com.sanchit.funda.utils.Constants;
import com.sanchit.funda.utils.DummyDataGenerator;
import com.sanchit.funda.utils.NumberUtils;
import com.sanchit.funda.utils.SecurityUtils;
import com.sanchit.funda.utils.ViewUtils;
import com.tom_roush.pdfbox.util.PDFBoxResourceLoader;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    // Request code for selecting a PDF document.
    private static final int PICK_PDF_FILE = 2;

    private final List<MFPosition> positions = new ArrayList<>();
    private final Map<String, MFPriceModel> priceMap = new HashMap<>();

    private final List<MFTrade> trades = new ArrayList<>();

    private RecyclerView recyclerViewHomeSummary1;
    private List<HomeSummary1Model> homeSummary1Model;
    private HomeSummary1Adapter homeSummary1Adapter;

    private RecyclerView recyclerViewHomeSummary2;
    private List<HomeSummary2Model> homeSummary2Model;
    private HomeSummary2Adapter homeSummary2Adapter;

    /*private RecyclerView recyclerViewHomeSummary3;
    private List<HomeSummary3Model> homeSummary3Model;
    private HomeSummary3Adapter homeSummary3Adapter;*/

    private ProgressBar spinner;

    private Integer priceRequestsPending = 0;

    private Uri ecasFilePath;
    private String PAN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setElevation(0);
        ViewUtils.setActionBarColor(this, R.color.colorPrimaryDark);
        setTitleColor(getResources().getColor(R.color.colorTextLight));
        setContentView(R.layout.activity_main);

        SecurityUtils.setupPermissions(this);

        spinner = (ProgressBar) findViewById(R.id.progressBar_home_summary);
        spinner.setVisibility(View.VISIBLE);

        ecasFilePath = (Uri) getIntent().getParcelableExtra("uri");
        PAN = getIntent().getStringExtra("PAN");

        PDFBoxResourceLoader.init(getApplicationContext());
        new NSDL_CASAsyncLoader(this, new OnECASFileLoadedHandler(), PAN).execute(ecasFilePath);
        new FundsRawDataAsyncLoader(this, new FundsRawDataLoadedHandler(this)).execute(Uri.EMPTY);

        recyclerViewHomeSummary1 = findViewById(R.id.recycler_view_home_summary_1);
        recyclerViewHomeSummary1.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        homeSummary1Model = DummyDataGenerator.generateHomeSumary1Model();
        homeSummary1Adapter = new HomeSummary1Adapter(this, homeSummary1Model);
        recyclerViewHomeSummary1.setAdapter(homeSummary1Adapter);

        recyclerViewHomeSummary2 = findViewById(R.id.recycler_view_home_summary_2);
        recyclerViewHomeSummary2.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        homeSummary2Model = DummyDataGenerator.generateHomeSumary2Model();
        homeSummary2Adapter = new HomeSummary2Adapter(this, homeSummary2Model);
        recyclerViewHomeSummary2.setAdapter(homeSummary2Adapter);

        /*recyclerViewHomeSummary3 = findViewById(R.id.recycler_view_home_summary_3);
        recyclerViewHomeSummary3.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        homeSummary3Model = DummyDataGenerator.generateHomeSumary3Model();
        homeSummary3Adapter = new HomeSummary3Adapter(this, homeSummary3Model);
        recyclerViewHomeSummary3.setAdapter(homeSummary3Adapter);*/
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        MFTrade trade = (MFTrade) intent.getSerializableExtra("trade");
        if (trade != null) {
            trades.add(trade);
            refreshSummary();

            new MFAPI_NAVAsyncLoader(this, new OnMFAPI_PriceLoadedHandler()).execute(trade.getFund().getAmfiID());
            ++priceRequestsPending;
        } else {
            Uri newEcasFilePath = (Uri) intent.getParcelableExtra("uri");
            String newPAN = intent.getStringExtra("PAN");

            if (!ecasFilePath.equals(newEcasFilePath) || !newPAN.equals(PAN)) {
                PAN = newPAN;
                ecasFilePath = newEcasFilePath;
                spinner.setVisibility(View.VISIBLE);
                new NSDL_CASAsyncLoader(this, new OnECASFileLoadedHandler(), PAN).execute(ecasFilePath);
                new FundsRawDataAsyncLoader(this, new FundsRawDataLoadedHandler(this)).execute(Uri.EMPTY);
            }
        }
    }

    private void refreshSummary() {
        BigDecimal valuation = BigDecimal.ZERO;
        BigDecimal investment = BigDecimal.ZERO;

        Set<String> funds = new HashSet<>();
        Set<String> categories = new HashSet<>();

        for (MFPosition position : positions) {
            MFPriceModel mfPriceModel = priceMap.get(position.getFund().getAmfiID());
            BigDecimal latestNAV = mfPriceModel == null ? BigDecimal.ZERO : mfPriceModel.getPrice(Constants.Duration.T);

            investment = investment.add(position.getCost());
            valuation = valuation.add(position.getQuantity().multiply(latestNAV));

            funds.add(position.getFund().getFundName());
            categories.add(position.getFund().getAppDefinedCategory());
        }
        for (MFTrade trade : trades) {
            MFPriceModel mfPriceModel = priceMap.get(trade.getFund().getAmfiID());
            BigDecimal latestNAV = mfPriceModel == null ? BigDecimal.ZERO : mfPriceModel.getPrice(Constants.Duration.T);

            investment = investment.add(trade.getCost());
            valuation = valuation.add(trade.getQuantity().multiply(latestNAV));

            funds.add(trade.getFund().getFundName());
            categories.add(trade.getFund().getAppDefinedCategory());
        }

        BigDecimal pnl = (valuation.subtract(investment)).divide(investment, 4, BigDecimal.ROUND_HALF_UP);

        ((TextView) findViewById(R.id.home_summary_investment)).setText(NumberUtils.formatMoney(investment));
        ((TextView) findViewById(R.id.home_summary_fund_count)).setText(String.valueOf(funds.size()));
        ((TextView) findViewById(R.id.home_summary_category_count)).setText(String.valueOf(categories.size()));
        ((TextView) findViewById(R.id.home_summary_valuation)).setText(NumberUtils.formatMoney(valuation));
        ((TextView) findViewById(R.id.home_summary_pnl)).setText(NumberUtils.toPercentage(pnl, 2));

        spinner.setVisibility(View.GONE);
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
            cache.add(fund.getFundName(), fund);
        }
        return cache;
    }

    private class OnECASFileLoadedHandler implements OnEnrichmentCompleted<List<MFPosition>> {
        @Override
        public void updateView(List<MFPosition> data) {
            if (data != null) {
                positions.addAll(data);
            }
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
            CacheManager.registerCache(Caches.FUNDS_BY_KEY, generateCacheByName(data));

            Map<String, MutualFund> isinToFundMap = toISINMap(data);
            updateFundDataIntoPositions(isinToFundMap);
            updateSummary();

            initiatePriceUpdateRequests();
        }

        private void initiatePriceUpdateRequests() {
            for (MFPosition position : positions) {
                String amfiID = position.getFund().getAmfiID();
                new MFAPI_NAVAsyncLoader(activity, new OnMFAPI_PriceLoadedHandler()).execute(amfiID);
                ++priceRequestsPending;
            }
        }

        private void updateFundDataIntoPositions(Map<String, MutualFund> isinToFundMap) {
            for (MFPosition position : positions) {
                position.setFund(isinToFundMap.get(position.getFund().getIsin()));
            }
        }

        private void updateSummary() {
            if (positions == null || positions.isEmpty()) {
                Toast.makeText(activity, "No Data", Toast.LENGTH_LONG).show();
                return;
            }

            BigDecimal investment = BigDecimal.ZERO;
            BigDecimal valuation = BigDecimal.ZERO;
            Set<String> funds = new HashSet<>();
            Set<String> categories = new HashSet<>();
            for (MFPosition position : positions) {
                investment = investment.add(position.getCost());
                valuation = valuation.add(position.getCurrentValue());
                funds.add(position.getFund().getFundName());
                categories.add(position.getFund().getAppDefinedCategory());
            }
            BigDecimal pnl = (valuation.subtract(investment)).divide(investment, 4, BigDecimal.ROUND_HALF_UP);

            ((TextView) findViewById(R.id.home_summary_investment)).setText(NumberUtils.formatMoney(investment));
            ((TextView) findViewById(R.id.home_summary_fund_count)).setText(String.valueOf(funds.size()));
            ((TextView) findViewById(R.id.home_summary_category_count)).setText(String.valueOf(categories.size()));
        }

        private Map<String, MutualFund> toISINMap(List<MutualFund> funds) {
            Map<String, MutualFund> isinToFundMap = new HashMap<>();
            for (MutualFund fund : funds) {
                isinToFundMap.put(fund.getIsin(), fund);
            }
            return isinToFundMap;
        }
    }

    private class OnMFAPI_PriceLoadedHandler implements OnEnrichmentCompleted<MFPriceModel> {
        @Override
        public void updateView(MFPriceModel data) {
            priceMap.put(data.getAmfiID(), data);
            --priceRequestsPending;

            if (priceRequestsPending == 0) {
                // Update the latest Valuation and enrich further data
                refreshSummary();
            }
        }
    }
}
