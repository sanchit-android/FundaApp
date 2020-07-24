package com.sanchit.funda.model;

import java.math.BigDecimal;

public class PositionViewModel {

    private String head;

    private BigDecimal investment = BigDecimal.ZERO;
    private BigDecimal valuation = BigDecimal.ZERO;
    private BigDecimal previousValuation = BigDecimal.ZERO;

    private BigDecimal totalCost;

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public BigDecimal getInvestment() {
        return investment;
    }

    public void addInvestment(BigDecimal v) {
        investment = investment.add(v);
    }

    public BigDecimal getValuation() {
        return valuation;
    }

    public void addValuation(BigDecimal v) {
        valuation = valuation.add(v);
    }

    public BigDecimal getPreviousValuation() {
        return previousValuation;
    }

    public void addPreviousValuation(BigDecimal v) {
        previousValuation = previousValuation.add(v);
    }

    public BigDecimal getPnlOverall() {
        return valuation.subtract(investment).divide(investment, 4, BigDecimal.ROUND_HALF_UP);
    }

    public BigDecimal getPnlDay() {
        return valuation.subtract(previousValuation).divide(previousValuation, 4, BigDecimal.ROUND_HALF_UP);
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }
}
