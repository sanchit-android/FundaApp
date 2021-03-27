package com.sanchit.funda.model.homesummary;

import com.sanchit.funda.model.MFPosition;
import com.sanchit.funda.model.MFTrade;
import com.sanchit.funda.utils.NumberUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssetClassBreakdownModel extends AbstractHomeSummary2Model {

    private final List<MFPosition> positions;
    private final List<MFTrade> trades;

    public AssetClassBreakdownModel(List<MFPosition> positions, List<MFTrade> trades) {
        super("Equity - Debt Ratio");
        this.positions = positions;
        this.trades = trades;

        initModel();
    }

    @Override
    protected void initModel() {
        Map<String, BigDecimal> totalsMap = new HashMap<>();
        for (MFPosition position : positions) {
            String category = position.getFund().getCategory();
            if (!totalsMap.containsKey(category)) {
                totalsMap.put(category, BigDecimal.ZERO);
            }
            totalsMap.put(category, totalsMap.get(category).add(position.getCost()));
        }
        for (MFTrade trade : trades) {
            String category = trade.getFund().getCategory();
            if (!totalsMap.containsKey(category)) {
                totalsMap.put(category, BigDecimal.ZERO);
            }
            totalsMap.put(category, totalsMap.get(category).add(trade.getCost()));
        }

        BigDecimal equity = totalsMap.get("Equity Scheme");
        BigDecimal debt = totalsMap.get("Debt Scheme");
        equity = equity == null ? BigDecimal.ZERO : equity;
        debt = debt == null ? BigDecimal.ZERO : debt;
        BigDecimal total = equity.add(debt);
        String equityPerc = NumberUtils.toPercentage(equity, total);
        String debtPerc = NumberUtils.toPercentage(debt, total);

        content = equityPerc + " : " + debtPerc;

        BigDecimal equityPVal = equity.divide(total, 3, BigDecimal.ROUND_HALF_UP);
        if (equityPVal.compareTo(new BigDecimal("0.8")) >= 0) {
            detail = "Equity Overweight";
        } else if (equityPVal.compareTo(new BigDecimal("0.6")) >= 0) {
            detail = "Equity Biased";
        } else if (equityPVal.compareTo(new BigDecimal("0.4")) >= 0) {
            detail = "Equity Debt Balanced";
        } else if (equityPVal.compareTo(new BigDecimal("0.2")) >= 0) {
            detail = "Debt Biased";
        } else {
            detail = "Debt Overweight";
        }
    }
}
