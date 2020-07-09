package com.sanchit.funda.model;

import java.math.BigDecimal;

public class MFPosition {

    private MutualFund fund;
    private BigDecimal quantity;
    private BigDecimal costPrice;
    private BigDecimal cost;

    private BigDecimal currentNAV;
    private BigDecimal currentValue;
    private BigDecimal unrealizedProfit;
    private BigDecimal annualizedReturn;

    private String folioNo;

    public MutualFund getFund() {
        return fund;
    }

    public void setFund(MutualFund fund) {
        this.fund = fund;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(BigDecimal costPrice) {
        this.costPrice = costPrice;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public BigDecimal getCurrentNAV() {
        return currentNAV;
    }

    public void setCurrentNAV(BigDecimal currentNAV) {
        this.currentNAV = currentNAV;
    }

    public BigDecimal getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(BigDecimal currentValue) {
        this.currentValue = currentValue;
    }

    public BigDecimal getUnrealizedProfit() {
        return unrealizedProfit;
    }

    public void setUnrealizedProfit(BigDecimal unrealizedProfit) {
        this.unrealizedProfit = unrealizedProfit;
    }

    public BigDecimal getAnnualizedReturn() {
        return annualizedReturn;
    }

    public void setAnnualizedReturn(BigDecimal annualizedReturn) {
        this.annualizedReturn = annualizedReturn;
    }

    public String getFolioNo() {
        return folioNo;
    }

    public void setFolioNo(String folioNo) {
        this.folioNo = folioNo;
    }
}
