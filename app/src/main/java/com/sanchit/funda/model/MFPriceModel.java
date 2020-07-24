package com.sanchit.funda.model;

import com.sanchit.funda.utils.NumberUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class MFPriceModel implements Serializable {
    private String amfiID;

    private Map<String, BigDecimal> priceMap = new HashMap<>();

    public MFPriceModel(String input) {
        this.amfiID = input;
    }

    public String getAmfiID() {
        return amfiID;
    }

    public void setAmfiID(String amfiID) {
        this.amfiID = amfiID;
    }

    public Map<String, BigDecimal> getPriceMap() {
        return priceMap;
    }

    public void setPriceMap(Map<String, BigDecimal> priceMap) {
        this.priceMap = priceMap;
    }

    public BigDecimal getPrice(String key) {
        return priceMap.get(key);
    }

    public String getPriceString(String key) {
        return NumberUtils.formatMoney(priceMap.get(key));
    }
}
