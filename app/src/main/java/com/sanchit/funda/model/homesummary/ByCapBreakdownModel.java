package com.sanchit.funda.model.homesummary;

import com.sanchit.funda.model.MFPosition;
import com.sanchit.funda.model.MFTrade;
import com.sanchit.funda.utils.NumberUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ByCapBreakdownModel extends AbstractHomeSummary2Model {

    private final List<MFPosition> positions;
    private final List<MFTrade> trades;

    public ByCapBreakdownModel(List<MFPosition> positions, List<MFTrade> trades) {
        super("Large - Mid - Small Cap Fund Ratio");
        this.positions = positions;
        this.trades = trades;

        initModel();
    }

    @Override
    protected void initModel() {
        Map<String, BigDecimal> totalsMap = new HashMap<>();
        for (MFPosition position : positions) {
            String category = position.getFund().getAppDefinedCategory();
            if (!totalsMap.containsKey(category)) {
                totalsMap.put(category, BigDecimal.ZERO);
            }
            totalsMap.put(category, totalsMap.get(category).add(position.getCost()));
        }
        for (MFTrade trade : trades) {
            String category = trade.getFund().getAppDefinedCategory();
            if (!totalsMap.containsKey(category)) {
                totalsMap.put(category, BigDecimal.ZERO);
            }
            totalsMap.put(category, totalsMap.get(category).add(trade.getCost()));
        }

        BigDecimal large = totalsMap.get("Large Cap Fund");
        BigDecimal mid = totalsMap.get("Mid Cap Fund");
        BigDecimal small = totalsMap.get("Small Cap Fund");
        BigDecimal largeAndMid = totalsMap.get("Large & Mid Cap Fund");
        large = large.add(largeAndMid.divide(new BigDecimal("2")));
        mid = mid.add(largeAndMid.divide(new BigDecimal("2")));
        BigDecimal total = large.add(mid).add(small);
        String largePerc = NumberUtils.toPercentage(large, total);
        String midPerc = NumberUtils.toPercentage(mid, total);
        String smallPerc = NumberUtils.toPercentage(small, total);

        content = largePerc + " : " + midPerc + " : " + smallPerc;

        BigDecimal largePVal = large.divide(total, 3, BigDecimal.ROUND_HALF_UP);
        BigDecimal midPVal = mid.divide(total, 3, BigDecimal.ROUND_HALF_UP);
        BigDecimal smallPVal = small.divide(total, 3, BigDecimal.ROUND_HALF_UP);
        if (largePVal.compareTo(new BigDecimal("0.8")) >= 0) {
            detail = "Large Cap Overweight";
        } else if (largePVal.compareTo(new BigDecimal("0.6")) >= 0) {
            detail = "Large Cap Biased";
        } else if (midPVal.compareTo(new BigDecimal("0.8")) >= 0) {
            detail = "Mid Cap Overweight";
        } else if (midPVal.compareTo(new BigDecimal("0.6")) >= 0) {
            detail = "Mid Cap Biased";
        } else if (smallPVal.compareTo(new BigDecimal("0.8")) >= 0) {
            detail = "Small Cap Overweight";
        } else if (smallPVal.compareTo(new BigDecimal("0.6")) >= 0) {
            detail = "Small Cap Biased";
        } else {
            detail = "Balanced by Cap";
        }
    }
}
