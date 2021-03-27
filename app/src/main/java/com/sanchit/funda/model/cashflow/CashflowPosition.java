package com.sanchit.funda.model.cashflow;

import com.sanchit.funda.model.MutualFund;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CashflowPosition {

    private MutualFund fund;
    private List<ClosedTaxlotGroup> realizedTaxlots = new ArrayList<>();
    private UnclosedTaxlotGroup unclosedTaxlotGroup;

    public MutualFund getFund() {
        return fund;
    }

    public void setFund(MutualFund fund) {
        this.fund = fund;
    }

    public List<ClosedTaxlotGroup> getRealizedTaxlots() {
        return realizedTaxlots;
    }

    public UnclosedTaxlotGroup getUnclosedTaxlotGroup() {
        return unclosedTaxlotGroup;
    }

    public void setUnclosedTaxlotGroup(UnclosedTaxlotGroup unclosedTaxlotGroup) {
        this.unclosedTaxlotGroup = unclosedTaxlotGroup;
    }

    public BigDecimal getCostPrice() {
        if (BigDecimal.ZERO.equals(unclosedTaxlotGroup.getUnrealizedQuantity())) {
            return BigDecimal.ZERO;
        }
        return unclosedTaxlotGroup.getCostOfUnrealizedInvestments().divide(unclosedTaxlotGroup.getUnrealizedQuantity(), 4, BigDecimal.ROUND_HALF_UP);
    }

    public BigDecimal getTotalRealizedPNL() {
        BigDecimal pnl = BigDecimal.ZERO;
        if (realizedTaxlots != null) {
            for (ClosedTaxlotGroup t : realizedTaxlots) {
                pnl = pnl.add(t.getRealizedPNL());
            }
        }
        pnl = pnl.add(unclosedTaxlotGroup.getRealizedPNL());
        return pnl;
    }
}
