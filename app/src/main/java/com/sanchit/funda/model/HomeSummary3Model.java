package com.sanchit.funda.model;

import com.sanchit.funda.utils.NumberUtils;

import java.math.BigDecimal;

public class HomeSummary3Model {

    private String index;
    private BigDecimal price;
    private BigDecimal change;

    public HomeSummary3Model(String index, BigDecimal price, BigDecimal change) {
        this.index = index;
        this.price = price;
        this.change = change;
    }

    public HomeSummary3Model() {
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getPriceString() {
        return NumberUtils.formatMoney(price);
    }

    public BigDecimal getChange() {
        return change;
    }

    public void setChange(BigDecimal change) {
        this.change = change;
    }

    public String getChangeString() {
        return NumberUtils.formatMoney(change);
    }
}
