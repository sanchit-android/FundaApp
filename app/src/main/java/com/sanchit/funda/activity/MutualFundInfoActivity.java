package com.sanchit.funda.activity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.sanchit.funda.R;
import com.sanchit.funda.async.MFAPI_NAVAsyncLoader;
import com.sanchit.funda.async.event.OnEnrichmentCompleted;
import com.sanchit.funda.cache.CacheManager;
import com.sanchit.funda.cache.Caches;
import com.sanchit.funda.model.MFDetailModel;
import com.sanchit.funda.model.MFPriceModel;
import com.sanchit.funda.model.MutualFund;
import com.sanchit.funda.utils.Constants;
import com.sanchit.funda.utils.ViewUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.sanchit.funda.utils.Constants.Duration.T_1M;
import static com.sanchit.funda.utils.Constants.Duration.T_1Y;
import static com.sanchit.funda.utils.Constants.Duration.T_3M;
import static com.sanchit.funda.utils.Constants.Duration.T_6M;
import static com.sanchit.funda.utils.Constants.getPositionDetailComparator;

public class MutualFundInfoActivity extends AppCompatActivity {

    private String amfiID;
    private String fundName;
    private CacheManager.Cache<String, MutualFund> funds;

    private List<MFDetailModel> fundDetailModels;

    private MFPriceModel priceData;
    private MutualFund fund;
    private int priceEnrichmentReqs = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        fundName = getIntent().getStringExtra("fundName");
        amfiID = getIntent().getStringExtra("amfiID");

        super.onCreate(savedInstanceState);
        setTitle(fundName);
        getSupportActionBar().setElevation(0);
        ViewUtils.setActionBarColor(this, R.color.colorPrimaryDark);
        setTitleColor(getResources().getColor(R.color.colorTextLight));
        setContentView(R.layout.activity_mutual_fund_info);

        funds = CacheManager.get(Caches.FUNDS_BY_AMFI_ID, MutualFund.class);

        setupData();
        setupFundDataViews();
    }

    private void setupData() {
        if (!funds.exists(amfiID)) {
            return;
        }

        fund = funds.get(amfiID);
        new MFAPI_NAVAsyncLoader(this, new OnMFAPI_PriceLoadedHandler()).execute(amfiID);

        for (MutualFund otherFund : funds) {
            if (fund.getAppDefinedCategory().equals(otherFund.getAppDefinedCategory())) {
                new MFAPI_NAVAsyncLoader(this, new RankHanlder(), MFAPI_NAVAsyncLoader.EnrichmentModel.Default)
                        .execute(otherFund.getAmfiID());
                ++priceEnrichmentReqs;
            }
        }
    }

    private void setupFundDataViews() {
        setTextViewData(R.id.mf_detail_fund_name, fund.getFundName());
    }

    private void setupFundPriceDependentViews() {
        setTextViewData(R.id.mf_detail_nav, priceData.getPriceString(Constants.Duration.T));

        setTextViewData(R.id.mf_detail_1M_return, priceData.get1MonthReturn());
        setTextViewData(R.id.mf_detail_3M_return, priceData.get3MonthsReturn());
        setTextViewData(R.id.mf_detail_6M_return, priceData.get6MonthsReturn());
        setTextViewData(R.id.mf_detail_1Y_return, priceData.get1YearReturn());
    }

    private void setupOtherFundsDependentViews() {
        setTextViewData(R.id.mf_detail_1M_rank, fund.getRankModel().getRank(T_1M).toString());
        setTextViewData(R.id.mf_detail_3M_rank, fund.getRankModel().getRank(T_3M).toString());
        setTextViewData(R.id.mf_detail_6M_rank, fund.getRankModel().getRank(T_6M).toString());
        setTextViewData(R.id.mf_detail_1Y_rank, fund.getRankModel().getRank(T_1Y).toString());
    }

    private void setTextViewData(int viewId, String fundName) {
        TextView view = findViewById(viewId);
        view.setText(fundName);
    }

    private class OnMFAPI_PriceLoadedHandler implements OnEnrichmentCompleted<MFPriceModel> {
        @Override
        public void updateView(MFPriceModel data) {
            priceData = data;
            setupFundPriceDependentViews();
        }
    }

    private class RankHanlder implements OnEnrichmentCompleted<MFPriceModel> {
        @Override
        public void updateView(MFPriceModel data) {
            MFDetailModel model = new MFDetailModel();
            model.setPriceModel(data);
            model.setFund(funds.get(data.getAmfiID()));
            fundDetailModels.add(model);

            if (priceEnrichmentReqs != 0) {
                return;
            }

            generateRanks(getPositionDetailComparator(getResources().getStringArray(R.array.sort_options)[7]), T_1Y);
            generateRanks(getPositionDetailComparator(getResources().getStringArray(R.array.sort_options)[5]), T_6M);
            generateRanks(getPositionDetailComparator(getResources().getStringArray(R.array.sort_options)[3]), T_3M);
            generateRanks(getPositionDetailComparator(getResources().getStringArray(R.array.sort_options)[1]), T_1M);
            setupOtherFundsDependentViews();
        }

        private void generateRanks(Comparator<MFDetailModel> comparator, String duration) {
            Collections.sort(fundDetailModels, comparator);
            int i = 1;
            for (MFDetailModel mfDetailModel : fundDetailModels) {
                mfDetailModel.getFund().getRankModel().setRank(duration, i++);
            }
        }
    }
}