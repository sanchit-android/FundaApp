package com.sanchit.funda.model;

import java.io.Serializable;

public class MutualFund implements Serializable {
    private String fundName;
    private String fundHouse;
    private String category;
    private String subCategory;
    private String appDefinedCategory;

    private String isin;
    private String amfiID;

    public String getFundName() {
        return fundName;
    }

    public void setFundName(String fundName) {
        this.fundName = fundName;
    }

    public String getFundHouse() {
        return fundHouse;
    }

    public void setFundHouse(String fundHouse) {
        this.fundHouse = fundHouse;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    public String getAppDefinedCategory() {
        return appDefinedCategory;
    }

    public void setAppDefinedCategory(String appDefinedCategory) {
        this.appDefinedCategory = appDefinedCategory;
    }

    public String getIsin() {
        return isin;
    }

    public void setIsin(String isin) {
        this.isin = isin;
    }

    public String getAmfiID() {
        return amfiID;
    }

    public void setAmfiID(String amfiID) {
        this.amfiID = amfiID;
    }
}
