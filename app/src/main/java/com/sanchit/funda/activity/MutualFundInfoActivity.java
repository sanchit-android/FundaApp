package com.sanchit.funda.activity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.sanchit.funda.R;
import com.sanchit.funda.async.FactSheetAsyncLoader;
import com.sanchit.funda.async.MFAPI_NAVAsyncLoader;
import com.sanchit.funda.async.event.OnEnrichmentCompleted;
import com.sanchit.funda.cache.CacheManager;
import com.sanchit.funda.cache.Caches;
import com.sanchit.funda.content.file.factsheet.SBIFactSheetParser;
import com.sanchit.funda.model.MFDetailModel;
import com.sanchit.funda.model.MFPriceModel;
import com.sanchit.funda.model.MutualFund;
import com.sanchit.funda.utils.Constants;
import com.sanchit.funda.utils.DateUtils;
import com.sanchit.funda.utils.ViewUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.sanchit.funda.utils.Constants.Duration.T_1M;
import static com.sanchit.funda.utils.Constants.Duration.T_1Y;
import static com.sanchit.funda.utils.Constants.Duration.T_2Y;
import static com.sanchit.funda.utils.Constants.Duration.T_3M;
import static com.sanchit.funda.utils.Constants.Duration.T_3Y;
import static com.sanchit.funda.utils.Constants.Duration.T_5Y;
import static com.sanchit.funda.utils.Constants.Duration.T_6M;
import static com.sanchit.funda.utils.Constants.getPositionDetailComparator;

public class MutualFundInfoActivity extends AppCompatActivity {

    private String amfiID;
    private String fundName;
    private CacheManager.Cache<String, MutualFund> funds;
    private CacheManager.Cache<String, MFPriceModel> prices;

    private List<MFDetailModel> fundDetailModels = new ArrayList<>();

    private MFPriceModel priceData;
    private MutualFund fund;
    private int priceEnrichmentReqs = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        fundName = getIntent().getStringExtra("fundName");
        amfiID = getIntent().getStringExtra("amfiID");

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setTitle(fundName);
        getSupportActionBar().setElevation(0);
        ViewUtils.setActionBarColor(this, R.color.colorPrimaryDark);
        setTitleColor(getResources().getColor(R.color.colorTextLight));
        setContentView(R.layout.activity_mutual_fund_info);

        funds = CacheManager.get(Caches.FUNDS_BY_AMFI_ID, MutualFund.class);
        prices = CacheManager.get(Caches.PRICES_BY_AMFI_ID, MFPriceModel.class);

        setupData();
        setupFundDataViews();
        setupHandlers();
    }

    private void setupHandlers() {
        findViewById(R.id.mf_detail_grwcht_3Y_option).setOnClickListener(new OnGrowthOptionListener(T_3Y));
        findViewById(R.id.mf_detail_grwcht_1Y_option).setOnClickListener(new OnGrowthOptionListener(T_1Y));
        findViewById(R.id.mf_detail_grwcht_6M_option).setOnClickListener(new OnGrowthOptionListener(T_6M));
        findViewById(R.id.mf_detail_grwcht_3M_option).setOnClickListener(new OnGrowthOptionListener(T_3M));
        findViewById(R.id.mf_detail_grwcht_1M_option).setOnClickListener(new OnGrowthOptionListener(T_1M));
    }

    private void setupData() {
        if (!funds.exists(amfiID)) {
            return;
        }

        fund = funds.get(amfiID);
        if (!prices.exists(fund.getAmfiID())) {
            new MFAPI_NAVAsyncLoader(this, new OnMFAPI_PriceLoadedHandler()).execute(amfiID);
        } else {
            priceData = prices.get(fund.getAmfiID());
            setupFundPriceDependentViews();
        }

        boolean priceRequestsFired = false;
        for (MutualFund otherFund : funds) {
            if (fund.getAppDefinedCategory().equals(otherFund.getAppDefinedCategory())) {
                if (!prices.exists(otherFund.getAmfiID())) {
                    new MFAPI_NAVAsyncLoader(this, new RankHandler(), MFAPI_NAVAsyncLoader.EnrichmentModel.Default)
                            .execute(otherFund.getAmfiID());
                    ++priceEnrichmentReqs;
                    priceRequestsFired = true;
                } else {
                    MFDetailModel model = new MFDetailModel();
                    model.setPriceModel(prices.get(otherFund.getAmfiID()));
                    model.setFund(otherFund);
                    fundDetailModels.add(model);
                }
            }
        }

        if (!priceRequestsFired) {
            generateRanks();
            setupOtherFundsDependentViews();
        }

        new FactSheetAsyncLoader(this, new OnFactSheetLoadedHandler(), new SBIFactSheetParser()).execute();
    }

    private void setupFundDataViews() {
        ViewUtils.setTextViewData(this, R.id.mf_detail_fund_name, fund.getFundName());
        ViewUtils.setTextViewData(this, R.id.mf_detail_category, fund.getCategory());
        ViewUtils.setTextViewData(this, R.id.mf_detail_sub_category, fund.getSubCategory());
    }

    private void setupFundPriceDependentViews() {
        ViewUtils.setTextViewData(this, R.id.mf_detail_nav, priceData.getPriceString(Constants.Duration.T));

        ViewUtils.setTextViewData(this, R.id.mf_detail_1M_return, priceData.get1MonthReturn());
        ViewUtils.setTextViewData(this, R.id.mf_detail_3M_return, priceData.get3MonthsReturn());
        ViewUtils.setTextViewData(this, R.id.mf_detail_6M_return, priceData.get6MonthsReturn());
        ViewUtils.setTextViewData(this, R.id.mf_detail_1Y_return, priceData.get1YearReturn());
        ViewUtils.setTextViewData(this, R.id.mf_detail_2Y_return, priceData.get2YearReturn());
        ViewUtils.setTextViewData(this, R.id.mf_detail_3Y_return, priceData.get3YearReturn());
        ViewUtils.setTextViewData(this, R.id.mf_detail_5Y_return, priceData.get5YearReturn());

        setupChartView(Constants.Duration.T_1Y);
    }

    private void setupChartView(String range) {
        LineChart chart = findViewById(R.id.mf_detail_growth_chart);
        List<MFPriceModel.NAVSnap> snaps = priceData.getNavSnaps();

        Calendar snap0Date = snaps.get(0).date;
        Constants.DurationData durationData = Constants.PRICE_MAP.get(range);
        Calendar snapTDate = DateUtils.customDate(durationData.getDurationType(), durationData.getDuration());

        List<Entry> entries = new ArrayList<>();
        int i = 0;
        for (int j = snaps.size()-1; j >= 0;j--) {
            MFPriceModel.NAVSnap snapJ = snaps.get(j);
            if(snapJ.date.before(snapTDate)) {
                continue;
            }
            entries.add(new Entry(++i, snapJ.price.floatValue()));
        }

        LineDataSet dataSet = new LineDataSet(entries, "");
        dataSet.setDrawCircles(false);
        dataSet.setColor(getResources().getColor(R.color.colorAccent));
        dataSet.setLineWidth(2);
        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.notifyDataSetChanged();
        chart.invalidate();
    }

    private void setupOtherFundsDependentViews() {
        ViewUtils.setTextViewData(this, R.id.mf_detail_1M_rank, fund.getRankModel().getRank(T_1M).toString());
        ViewUtils.setTextViewData(this, R.id.mf_detail_3M_rank, fund.getRankModel().getRank(T_3M).toString());
        ViewUtils.setTextViewData(this, R.id.mf_detail_6M_rank, fund.getRankModel().getRank(T_6M).toString());
        ViewUtils.setTextViewData(this, R.id.mf_detail_1Y_rank, fund.getRankModel().getRank(T_1Y).toString());
        ViewUtils.setTextViewData(this, R.id.mf_detail_2Y_rank, fund.getRankModel().getRank(T_2Y).toString());
        ViewUtils.setTextViewData(this, R.id.mf_detail_3Y_rank, fund.getRankModel().getRank(T_3Y).toString());
        ViewUtils.setTextViewData(this, R.id.mf_detail_5Y_rank, fund.getRankModel().getRank(T_5Y).toString());
    }

    private void setupFactSheetDependentViews() {
        ViewUtils.setTextViewData(this, R.id.mf_detail_benchmark, fund.getBenchmark());
        ViewUtils.setTextViewData(this, R.id.mf_detail_aaum, fund.getAaum());
    }

    public void generateRanks() {
        generateRanks(getPositionDetailComparator(getResources().getStringArray(R.array.sort_options)[6]), T_5Y);
        generateRanks(getPositionDetailComparator(getResources().getStringArray(R.array.sort_options)[5]), T_3Y);
        generateRanks(getPositionDetailComparator(getResources().getStringArray(R.array.sort_options)[4]), T_2Y);
        generateRanks(getPositionDetailComparator(getResources().getStringArray(R.array.sort_options)[3]), T_1Y);
        generateRanks(getPositionDetailComparator(getResources().getStringArray(R.array.sort_options)[2]), T_6M);
        generateRanks(getPositionDetailComparator(getResources().getStringArray(R.array.sort_options)[1]), T_3M);
        generateRanks(getPositionDetailComparator(getResources().getStringArray(R.array.sort_options)[0]), T_1M);
    }

    private void generateRanks(Comparator<MFDetailModel> comparator, String duration) {
        Collections.sort(fundDetailModels, comparator);
        int i = 1;
        for (MFDetailModel mfDetailModel : fundDetailModels) {
            mfDetailModel.getFund().getRankModel().setRank(duration, i++);
        }
    }

    private class OnMFAPI_PriceLoadedHandler implements OnEnrichmentCompleted<MFPriceModel> {
        @Override
        public void updateView(MFPriceModel data) {
            priceData = data;
            setupFundPriceDependentViews();
        }
    }

    private class RankHandler implements OnEnrichmentCompleted<MFPriceModel> {
        @Override
        public void updateView(MFPriceModel data) {
            --priceEnrichmentReqs;

            MFDetailModel model = new MFDetailModel();
            model.setPriceModel(data);
            model.setFund(funds.get(data.getAmfiID()));
            fundDetailModels.add(model);

            if (priceEnrichmentReqs != 0) {
                return;
            }

            generateRanks();
            setupOtherFundsDependentViews();
        }
    }

    private class OnFactSheetLoadedHandler implements OnEnrichmentCompleted<Void> {
        @Override
        public void updateView(Void data) {
            setupFactSheetDependentViews();
        }
    }

    private class OnGrowthOptionListener implements View.OnClickListener {
        private final String duration;

        public OnGrowthOptionListener(String duration) {
            this.duration = duration;
        }

        @Override
        public void onClick(View v) {
            setupChartView(duration);
        }
    }
}