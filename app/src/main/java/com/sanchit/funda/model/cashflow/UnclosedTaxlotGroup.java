package com.sanchit.funda.model.cashflow;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UnclosedTaxlotGroup {
    private List<Taxlot> openingLots = new ArrayList<>();
    private List<Taxlot> closingLots = new ArrayList<>();

    private BigDecimal openingCost;
    private BigDecimal closingCost;

    private BigDecimal realizedQuantity;
    private BigDecimal realizedPNL;

    private BigDecimal unrealizedQuantity;
    private BigDecimal costOfUnrealizedInvestments;

    public List<Taxlot> getOpeningLots() {
        return openingLots;
    }

    public void setOpeningLots(List<Taxlot> openingLots) {
        this.openingLots = openingLots;
    }

    public List<Taxlot> getClosingLots() {
        return closingLots;
    }

    public void setClosingLots(List<Taxlot> closingLots) {
        this.closingLots = closingLots;
    }

    public BigDecimal getOpeningCost() {
        return openingCost;
    }

    public void setOpeningCost(BigDecimal openingCost) {
        this.openingCost = openingCost;
    }

    public BigDecimal getClosingCost() {
        return closingCost;
    }

    public void setClosingCost(BigDecimal closingCost) {
        this.closingCost = closingCost;
    }

    public BigDecimal getRealizedQuantity() {
        return realizedQuantity;
    }

    public void setRealizedQuantity(BigDecimal realizedQuantity) {
        this.realizedQuantity = realizedQuantity;
    }

    public BigDecimal getRealizedPNL() {
        return realizedPNL;
    }

    public void setRealizedPNL(BigDecimal realizedPNL) {
        this.realizedPNL = realizedPNL;
    }

    public BigDecimal getUnrealizedQuantity() {
        return unrealizedQuantity;
    }

    public void setUnrealizedQuantity(BigDecimal unrealizedQuantity) {
        this.unrealizedQuantity = unrealizedQuantity;
    }

    public BigDecimal getCostOfUnrealizedInvestments() {
        return costOfUnrealizedInvestments;
    }

    public void setCostOfUnrealizedInvestments(BigDecimal costOfUnrealizedInvestments) {
        this.costOfUnrealizedInvestments = costOfUnrealizedInvestments;
    }

    public List<Taxlot> getUnrealizedTaxlots() {
        List<Taxlot> lots = new ArrayList<>();
        lots.addAll(openingLots);
        lots.addAll(closingLots);
        Collections.sort(lots, (o1, o2) -> o2.getInvestmentDate().compareTo(o1.getInvestmentDate()));
        return lots;
    }
}
