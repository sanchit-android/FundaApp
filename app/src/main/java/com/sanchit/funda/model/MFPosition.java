package com.sanchit.funda.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class MFPosition extends AbstractMFObject implements Serializable {

    // As specified in NSDL-CAS
    private BigDecimal currentNAV;
    private BigDecimal currentValue;
    private BigDecimal unrealizedProfit;
    private BigDecimal annualizedReturn;

    // As per latest prices for MF held
    private BigDecimal pnlOverall;
    private BigDecimal pnlDay;
    private BigDecimal valuation;

    private String folioNo;

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

    public BigDecimal getValuation() {
        return valuation;
    }

    public void setValuation(BigDecimal valuation) {
        this.valuation = valuation;
    }
}
