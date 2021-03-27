package com.sanchit.funda.model.homesummary;

import com.sanchit.funda.model.MFPosition;
import com.sanchit.funda.model.MFTrade;
import com.sanchit.funda.model.MutualFund;
import com.sanchit.funda.utils.NumberUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ByCategoryFundExposureModel extends AbstractHomeSummary2Model {

    private final List<MFPosition> positions;
    private final List<MFTrade> trades;
    private final String categoryValue;
    private final Criteria criteria;

    public ByCategoryFundExposureModel(String header, String categoryValue, Criteria criteria, List<MFPosition> positions, List<MFTrade> trades) {
        super(header);
        this.positions = positions;
        this.trades = trades;
        this.categoryValue = categoryValue;
        this.criteria = criteria;

        initModel();
    }

    @Override
    protected void initModel() {

        Map<String, BigDecimal> totalsMap = new HashMap<>();
        for (MFPosition position : positions) {
            String category = criteria.get(position.getFund());
            if (!totalsMap.containsKey(category)) {
                totalsMap.put(category, BigDecimal.ZERO);
            }
            totalsMap.put(category, totalsMap.get(category).add(position.getCost()));
        }
        for (MFTrade trade : trades) {
            String category = criteria.get(trade.getFund());
            if (!totalsMap.containsKey(category)) {
                totalsMap.put(category, BigDecimal.ZERO);
            }
            totalsMap.put(category, totalsMap.get(category).add(trade.getCost()));
        }

        BigDecimal allTotals = totalsMap.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal categoryTotal = totalsMap.get(categoryValue);
        content = NumberUtils.toPercentage(categoryTotal, allTotals);

        BigDecimal equityPVal = NumberUtils.safeDivide(categoryTotal, allTotals, 3);
        if (equityPVal.compareTo(new BigDecimal("0.8")) >= 0) {
            detail = categoryValue + " Overweight - Very High Risk";
        } else if (equityPVal.compareTo(new BigDecimal("0.6")) >= 0) {
            detail = categoryValue + " Biased - High Risk";
        } else if (equityPVal.compareTo(new BigDecimal("0.35")) >= 0) {
            detail = categoryValue + " Exposed - Over Hedged";
        } else if (equityPVal.compareTo(new BigDecimal("0.1")) >= 0) {
            detail = categoryValue + " Exposed - Balanced Hedged";
        } else if (equityPVal.compareTo(new BigDecimal("0")) > 0) {
            detail = categoryValue + " Exposed - UnderHedged";
        } else {
            detail = "No " + categoryValue + " Exposure";
        }
    }

    public interface Criteria {
        Criteria FundCategoryCriteria = (fund) -> fund.getCategory();
        Criteria FundSubCategoryCriteria = (fund) -> fund.getSubCategory();
        Criteria FundAppDefCategoryCriteria = (fund) -> fund.getAppDefinedCategory();
        Criteria FundHouseCriteria = (fund) -> fund.getFundHouse();
        Criteria FundNameCriteria = (fund) -> fund.getFundName();

        String get(MutualFund fund);
    }
}