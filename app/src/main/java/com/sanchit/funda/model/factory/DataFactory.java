package com.sanchit.funda.model.factory;

import com.sanchit.funda.model.MFDetailModel;
import com.sanchit.funda.model.MFPosition;
import com.sanchit.funda.model.MFTrade;
import com.sanchit.funda.model.homesummary.AbstractHomeSummary1Model;
import com.sanchit.funda.model.homesummary.AbstractHomeSummary2Model;
import com.sanchit.funda.model.homesummary.AssetClassBreakdownModel;
import com.sanchit.funda.model.homesummary.ByCapBreakdownModel;
import com.sanchit.funda.model.homesummary.ByCategoryFundExposureModel;
import com.sanchit.funda.model.homesummary.TopFundModel;
import com.sanchit.funda.model.homesummary.TopGrowthModel;
import com.sanchit.funda.model.homesummary.TopPerformingModel;

import java.util.ArrayList;
import java.util.List;

public class DataFactory {

    public static List<AbstractHomeSummary1Model> generateHomeSummary1Model(List<MFPosition> positions, List<MFTrade> trades) {
        List<AbstractHomeSummary1Model> model = new ArrayList<>();
        model.add(new TopPerformingModel("Top Performing Fund", AbstractHomeSummary1Model.KeyProvider.FundNameKey, positions, trades));
        model.add(new TopPerformingModel("Top Performing Category", AbstractHomeSummary1Model.KeyProvider.CustomCategoryKey, positions, trades));
        model.add(new TopPerformingModel("Least Performing Fund", AbstractHomeSummary1Model.KeyProvider.FundNameKey, true, positions, trades));
        model.add(new TopPerformingModel("Least Performing Category", AbstractHomeSummary1Model.KeyProvider.CustomCategoryKey, true, positions, trades));

        model.add(new TopGrowthModel("Top Growth in 1Y - Fund", AbstractHomeSummary1Model.KeyProvider.FundNameKey, positions, trades));
        model.add(new TopGrowthModel("Top Growth in 1Y - Category", AbstractHomeSummary1Model.KeyProvider.CustomCategoryKey, positions, trades));
        //model.add(new TopGrowthModel("Top Growth in 6M - Fund", AbstractHomeSummary1Model.KeyProvider.FundNameKey, positions, trades, Constants.Duration.T_6M));
        //model.add(new TopGrowthModel("Top Growth in 6M - Category", AbstractHomeSummary1Model.KeyProvider.CustomCategoryKey, positions, trades, Constants.Duration.T_6M));
        return model;
    }

    public static List<AbstractHomeSummary2Model> generateHomeSummary2Model(List<MFPosition> positions, List<MFTrade> trades) {
        List<AbstractHomeSummary2Model> model = new ArrayList<>();
        model.add(new AssetClassBreakdownModel(positions, trades));
        model.add(new ByCapBreakdownModel(positions, trades));
        model.add(new ByCategoryFundExposureModel("FoF Overseas Exposure", "FoF Overseas", ByCategoryFundExposureModel.Criteria.FundSubCategoryCriteria, positions, trades));
        model.add(new ByCategoryFundExposureModel("Gold Exposure", "Gold Fund", ByCategoryFundExposureModel.Criteria.FundAppDefCategoryCriteria, positions, trades));
        return model;
    }

    public static List<TopFundModel> generateHomeSummaryTopFundsModel(List<MFDetailModel> mfDetailModelList) {
        List<TopFundModel> response = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            TopFundModel topFundModel = new TopFundModel();
            MFDetailModel mfDetailModel = mfDetailModelList.get(i);
            topFundModel.setFundName(mfDetailModel.getFund().getFundNameComplete());
            topFundModel.setReturns(mfDetailModel.getPriceModel().get1YearReturn());
            response.add(topFundModel);
        }
        return response;
    }
}
