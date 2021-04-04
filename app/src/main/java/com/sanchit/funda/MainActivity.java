package com.sanchit.funda;

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
import com.sanchit.funda.async.MFAPI_NAVAsyncLoader;
import com.sanchit.funda.async.event.main_activity.FundsRawDataLoadedHandler;
import com.sanchit.funda.async.event.main_activity.OnCashflowDataLoadedHandler;
import com.sanchit.funda.async.event.main_activity.OnMFAPI_PriceLoadedHandler;
import com.sanchit.funda.async.event.main_activity.OnMFDetailModelLoadedHandler;
import com.sanchit.funda.cache.CacheManager;
import com.sanchit.funda.cache.Caches;
import com.sanchit.funda.dao.MutualFundDao;
import com.sanchit.funda.database.AppDatabase;
import com.sanchit.funda.database.DatabaseHelper;
import com.sanchit.funda.model.MFDetailModel;
import com.sanchit.funda.model.MFPosition;
import com.sanchit.funda.model.MFPriceModel;
import com.sanchit.funda.model.MFTrade;
import com.sanchit.funda.model.factory.MFDetailModelFactory;
import com.sanchit.funda.model.homesummary.AbstractHomeSummary1Model;
import com.sanchit.funda.model.homesummary.AbstractHomeSummary2Model;
import com.sanchit.funda.model.homesummary.TopFundModel;
import com.sanchit.funda.utils.SecurityUtils;
import com.sanchit.funda.utils.ViewUtils;
import com.tom_roush.pdfbox.util.PDFBoxResourceLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public static final String DEFAULT_CATEGORY = "Large Cap Fund";

    public List<MFPosition> positions = new ArrayList<>();
    public Map<String, MFPriceModel> priceMap = new HashMap<>();
    public List<MFTrade> trades = new ArrayList<>();
    public List<MFDetailModel> mfDetailModel = new ArrayList<>();

    public RecyclerView recyclerViewHomeSummary1;
    public List<AbstractHomeSummary1Model> homeSummary1Model;
    public HomeSummary1Adapter homeSummary1Adapter;

    public RecyclerView recyclerViewHomeSummary2;
    public List<AbstractHomeSummary2Model> homeSummary2Model;
    public HomeSummary2Adapter homeSummary2Adapter;

    public ProgressBar progressBarHomeSummary;
    public ProgressBar progressBarTopFunds;

    public Integer priceRequestsPending = 0;

    public Uri ecasFilePath;
    public String PAN;
    public String fileType;
    public RecyclerView recylerViewTopFunds;
    public List<TopFundModel> topFundsModel;
    public HomeSummaryTopFundsAdapter homeSummaryTopFundsAdapter;
    private String name;
    private Spinner spinnerFundCategories;

    public long startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startTime = System.currentTimeMillis();

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
        MutualFundDao mutualFundDao = AppDatabase.getInstance(this).mutualFundDao();
        DatabaseHelper.selectAll(mutualFundDao, new FundsRawDataLoadedHandler(this));

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
            new MFAPI_NAVAsyncLoader(this, new OnMFAPI_PriceLoadedHandler(this)).execute(trade.getFund().getAmfiID());
            new CashflowAsyncLoader(this, new OnCashflowDataLoadedHandler(this), trades).execute();
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

                MutualFundDao mutualFundDao = AppDatabase.getInstance(this).mutualFundDao();
                DatabaseHelper.selectAll(mutualFundDao, new FundsRawDataLoadedHandler(this));
            }
        }
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
        if (CacheManager.get(Caches.FUNDS) != null) {
            homeSummaryTopFundsAdapter.setGrouping(item);
            new MFDetailModelFactory(item, MFDetailModelFactory.Criteria.FundAppDefCategoryCriteria, new OnMFDetailModelLoadedHandler(this));
            progressBarTopFunds.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}
