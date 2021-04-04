package com.sanchit.funda.model;

import com.sanchit.funda.dao.entity.MutualFundModel;

import java.io.Serializable;

public class MutualFund implements Serializable {
    private String fundName;
    private String fundHouse;
    private String category;
    private String subCategory;
    private String appDefinedCategory;

    private String isin;
    private String amfiID;

    private String benchmark;
    private String aaum;
    private String fundManager;
    private String exitLoad;
    private String fundAim;

    private boolean isDirect;

    private MFRankModel rankModel = new MFRankModel();

    public MFRankModel getRankModel() {
        return rankModel;
    }

    public String getFundName() {
        return fundName;
    }

    public void setDirect(boolean direct) {
        isDirect = direct;
    }

    public boolean isDirect() {
        return isDirect;
    }

    public void setFundName(String fundName) {
        this.fundName = fundName;
    }

    public String getFundNameComplete() {
        return fundName + " - " + (isDirect ? "Direct Plan" : "Regular Plan");
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

    public String getBenchmark() {
        return benchmark;
    }

    public void setBenchmark(String benchmark) {
        this.benchmark = benchmark;
    }

    public String getAaum() {
        return aaum;
    }

    public void setAaum(String aaum) {
        this.aaum = aaum;
    }

    public String getFundManager() {
        return fundManager;
    }

    public void setFundManager(String fundManager) {
        this.fundManager = fundManager;
    }

    public String getExitLoad() {
        return exitLoad;
    }

    public void setExitLoad(String exitLoad) {
        this.exitLoad = exitLoad;
    }

    public String getFundAim() {
        return fundAim;
    }

    public void setFundAim(String fundAim) {
        this.fundAim = fundAim;
    }

    public MutualFund(MutualFundModel dbModel) {
        this.fundName = dbModel.fundName;
        this.fundHouse = dbModel.fundHouse;
        this.category = dbModel.category;
        this.subCategory = dbModel.subCategory;
        this.appDefinedCategory = dbModel.appDefinedCategory;
        this.isin = dbModel.isin;
        this.amfiID = dbModel.amfiID;
        this.benchmark = dbModel.benchmark;
        this.aaum = dbModel.aaum;
        this.fundManager = dbModel.fundManager;
        this.exitLoad = dbModel.exitLoad;
        this.fundAim = dbModel.fundAim;
        this.isDirect = dbModel.isDirect;
    }

    public MutualFund() {}

    @Override
    public String toString() {
        return "MutualFund{" +
                "fundName='" + fundName + '\'' +
                ", fundHouse='" + fundHouse + '\'' +
                ", category='" + category + '\'' +
                ", subCategory='" + subCategory + '\'' +
                ", appDefinedCategory='" + appDefinedCategory + '\'' +
                ", isin='" + isin + '\'' +
                ", amfiID='" + amfiID + '\'' +
                ", benchmark='" + benchmark + '\'' +
                ", aaum='" + aaum + '\'' +
                ", fundManager='" + fundManager + '\'' +
                ", exitLoad='" + exitLoad + '\'' +
                ", fundAim='" + fundAim + '\'' +
                ", isDirect=" + isDirect +
                ", rankModel=" + rankModel +
                '}';
    }
}
