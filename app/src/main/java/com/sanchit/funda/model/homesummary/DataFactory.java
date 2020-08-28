package com.sanchit.funda.model.homesummary;

import com.sanchit.funda.model.MFPosition;
import com.sanchit.funda.model.MFTrade;
import com.sanchit.funda.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class DataFactory {

    public static List<AbstractHomeSummary1Model> generateHomeSumary1Model(List<MFPosition> positions, List<MFTrade> trades) {
        List<AbstractHomeSummary1Model> model = new ArrayList<>();
        model.add(new TopPerformingModel("Top Performing Fund", AbstractHomeSummary1Model.KeyProvider.FundNameKey, positions, trades));
        model.add(new TopPerformingModel("Top Performing Category", AbstractHomeSummary1Model.KeyProvider.CustomCategoryKey, positions, trades));
        model.add(new TopPerformingModel("Least Performing Fund", AbstractHomeSummary1Model.KeyProvider.FundNameKey, true, positions, trades));
        model.add(new TopPerformingModel("Least Performing Category", AbstractHomeSummary1Model.KeyProvider.CustomCategoryKey, true, positions, trades));

        model.add(new TopGrowthModel("Top Growth in 1Y - Fund", AbstractHomeSummary1Model.KeyProvider.FundNameKey, positions, trades));
        model.add(new TopGrowthModel("Top Growth in 1Y - Category", AbstractHomeSummary1Model.KeyProvider.CustomCategoryKey, positions, trades));
        model.add(new TopGrowthModel("Top Growth in 6M - Fund", AbstractHomeSummary1Model.KeyProvider.FundNameKey, positions, trades, Constants.Duration.T_6M));
        model.add(new TopGrowthModel("Top Growth in 6M - Category", AbstractHomeSummary1Model.KeyProvider.CustomCategoryKey, positions, trades, Constants.Duration.T_6M));
        return model;
    }
}
