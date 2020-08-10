package com.sanchit.funda.model;

public class MFDetailModel {

    private MutualFund fund;
    private MFPriceModel priceModel;

    public MutualFund getFund() {
        return fund;
    }

    public void setFund(MutualFund fund) {
        this.fund = fund;
    }

    public MFPriceModel getPriceModel() {
        return priceModel;
    }

    public void setPriceModel(MFPriceModel priceModel) {
        this.priceModel = priceModel;
    }
}
