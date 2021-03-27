package com.sanchit.funda.model.factory;

import android.app.Activity;
import android.content.Context;

import com.sanchit.funda.R;
import com.sanchit.funda.activity.PositionLineItemDetailActivity;
import com.sanchit.funda.async.MFAPI_NAVAsyncLoader;
import com.sanchit.funda.async.event.OnEnrichmentCompleted;
import com.sanchit.funda.cache.CacheManager;
import com.sanchit.funda.cache.Caches;
import com.sanchit.funda.model.MFDetailModel;
import com.sanchit.funda.model.MFPriceModel;
import com.sanchit.funda.model.MutualFund;

import java.util.ArrayList;
import java.util.List;

public class MFDetailModelFactory {

    private final Criteria criteria;
    private final OnCompletionHandler completionHandler;
    private final String baseValue;

    List<MFDetailModel> fundDetailModels = new ArrayList<>();
    private int priceEnrichmentReqs = 0;

    public MFDetailModelFactory(String baseValue, Criteria criteria, OnCompletionHandler completionHandler) {
        this.baseValue = baseValue;
        this.criteria = criteria;
        this.completionHandler = completionHandler;

        generateDataModel();
    }

    public void generateDataModel() {
        List<MutualFund> funds = (List<MutualFund>) CacheManager.get(Caches.FUNDS);
        for (MutualFund fund : funds) {
            if (criteria.included(fund, baseValue)) {
                MFDetailModel mfDetailModel = new MFDetailModel();
                mfDetailModel.setFund(fund);
                fundDetailModels.add(mfDetailModel);

                new MFAPI_NAVAsyncLoader(null, new OnMFAPI_PriceLoadedHandler(mfDetailModel), MFAPI_NAVAsyncLoader.EnrichmentModel.Default)
                        .execute(mfDetailModel.getFund().getAmfiID());
                ++priceEnrichmentReqs;
            }
        }
    }

    public static MFDetailModelFactory.Criteria getCriteria(Context context, String grouping) {
        if (context.getResources().getStringArray(R.array.positions_view_grouping)[0].equals(grouping)) {
            return MFDetailModelFactory.Criteria.FundNameCriteria;
        } else if (context.getResources().getStringArray(R.array.positions_view_grouping)[1].equals(grouping)) {
            return MFDetailModelFactory.Criteria.FundCategoryCriteria;
        } else if (context.getResources().getStringArray(R.array.positions_view_grouping)[2].equals(grouping)) {
            return MFDetailModelFactory.Criteria.FundSubCategoryCriteria;
        } else if (context.getResources().getStringArray(R.array.positions_view_grouping)[3].equals(grouping)) {
            return MFDetailModelFactory.Criteria.FundAppDefCategoryCriteria;
        }
        return MFDetailModelFactory.Criteria.FundAppDefCategoryCriteria;
    }

    public interface OnCompletionHandler {
        void onMFDetailDatasetLoaded(List<MFDetailModel> mfDetailModelList);
    }

    public interface Criteria {
        Criteria FundCategoryCriteria = (fund, baseValue) -> fund.getCategory().equals(baseValue);
        Criteria FundSubCategoryCriteria = (fund, baseValue) -> fund.getSubCategory().equals(baseValue);
        Criteria FundAppDefCategoryCriteria = (fund, baseValue) -> fund.getAppDefinedCategory().equals(baseValue);
        Criteria FundHouseCriteria = (fund, baseValue) -> fund.getFundHouse().equals(baseValue);
        Criteria FundNameCriteria = (fund, baseValue) -> fund.getFundName().equals(baseValue);

        boolean included(MutualFund fund, String baseValue);
    }

    private class OnMFAPI_PriceLoadedHandler implements OnEnrichmentCompleted<MFPriceModel> {
        private final MFDetailModel model;

        public OnMFAPI_PriceLoadedHandler(MFDetailModel model) {
            this.model = model;
        }

        @Override
        public void updateView(MFPriceModel data) {
            model.setPriceModel(data);
            --priceEnrichmentReqs;

            if (priceEnrichmentReqs == 0) {
                completionHandler.onMFDetailDatasetLoaded(fundDetailModels);
            }
        }
    }
}
