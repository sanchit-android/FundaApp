package com.sanchit.funda.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class MFTrade implements Serializable {

    private MutualFund fund;
    private BigDecimal quantity;
    private BigDecimal costPrice;
    private BigDecimal cost;
    private Date investmentDate;

    private BigDecimal currentNAV;
    private BigDecimal valuation;

    // As per latest prices for MF held
    private BigDecimal pnlOverall;
    private BigDecimal pnlDay;

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

    public BigDecimal getValuation() {
        return valuation;
    }

    public void setValuation(BigDecimal valuation) {
        this.valuation = valuation;
    }

    public BigDecimal getPnlOverall() {
        return pnlOverall;
    }

    public void setPnlOverall(BigDecimal pnlOverall) {
        this.pnlOverall = pnlOverall;
    }

    public BigDecimal getPnlDay() {
        return pnlDay;
    }

    public void setPnlDay(BigDecimal pnlDay) {
        this.pnlDay = pnlDay;
    }

    public String getFolioNo() {
        return folioNo;
    }

    public void setFolioNo(String folioNo) {
        this.folioNo = folioNo;
    }

    public Date getInvestmentDate() {
        return investmentDate;
    }

    public void setInvestmentDate(Date investmentDate) {
        this.investmentDate = investmentDate;
    }
}
