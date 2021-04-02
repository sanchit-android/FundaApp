package com.sanchit.funda.model;

import com.sanchit.funda.utils.Constants;
import com.sanchit.funda.utils.NumberUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MFPriceModel implements Serializable {
    private String amfiID;

    private Date latestNAVDate;

    private List<NAVSnap> navSnaps = new ArrayList<>();

    public List<NAVSnap> getNavSnaps() {
        return navSnaps;
    }

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

    public String getPriceString(String key, int rounding) {
        return NumberUtils.formatMoney(priceMap.get(key), rounding);
    }

    public String get1YearReturn() {
        return getReturns(Constants.Duration.T_1Y, Constants.Duration.T);
    }

    public BigDecimal get1YearReturnComparable() {
        return getComparableReturns(Constants.Duration.T_1Y, Constants.Duration.T);
    }

    public String get2YearReturn() {
        return getReturns(Constants.Duration.T_2Y, Constants.Duration.T);
    }

    public BigDecimal get2YearReturnComparable() {
        return getComparableReturns(Constants.Duration.T_2Y, Constants.Duration.T);
    }

    public String get3YearReturn() {
        return getReturns(Constants.Duration.T_3Y, Constants.Duration.T);
    }

    public BigDecimal get3YearReturnComparable() {
        return getComparableReturns(Constants.Duration.T_3Y, Constants.Duration.T);
    }

    public String get5YearReturn() {
        return getReturns(Constants.Duration.T_5Y, Constants.Duration.T);
    }

    public BigDecimal get5YearReturnComparable() {
        return getComparableReturns(Constants.Duration.T_5Y, Constants.Duration.T);
    }

    public String get6MonthsReturn() {
        return getReturns(Constants.Duration.T_6M, Constants.Duration.T);
    }

    public BigDecimal get6MonthsReturnComparable() {
        return getComparableReturns(Constants.Duration.T_6M, Constants.Duration.T);
    }

    public String get3MonthsReturn() {
        return getReturns(Constants.Duration.T_3M, Constants.Duration.T);
    }

    public BigDecimal get3MonthsReturnComparable() {
        return getComparableReturns(Constants.Duration.T_3M, Constants.Duration.T);
    }

    public String get1MonthReturn() {
        return getReturns(Constants.Duration.T_1M, Constants.Duration.T);
    }

    public BigDecimal get1MonthReturnComparable() {
        return getComparableReturns(Constants.Duration.T_1M, Constants.Duration.T);
    }

    private String getReturns(String tOld, String tNow) {
        BigDecimal priceOld = getPrice(tOld);
        BigDecimal priceNow = getPrice(tNow);
        if (priceOld == null) {
            return "-";
        }
        return NumberUtils.toPercentage((priceNow.subtract(priceOld)).divide(priceOld, 4, BigDecimal.ROUND_HALF_UP), 2);
    }


    private BigDecimal getComparableReturns(String tOld, String tNow) {
        BigDecimal priceOld = getPrice(tOld);
        BigDecimal priceNow = getPrice(tNow);
        if (priceOld == null) {
            return BigDecimal.ZERO;
        }
        return (priceNow.subtract(priceOld)).divide(priceOld, 4, BigDecimal.ROUND_HALF_UP);
    }

    public Date getLatestNAVDate() {
        return latestNAVDate;
    }

    public void setLatestNAVDate(Date latestNAVDate) {
        this.latestNAVDate = latestNAVDate;
    }

    public static final class NAVSnap {
        public BigDecimal price;
        public Calendar date;

        public NAVSnap(BigDecimal nav, Calendar cal) {
            this.price = nav;
            this.date = cal;
        }
    }
}
