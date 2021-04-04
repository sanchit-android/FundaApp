package com.sanchit.funda.dao.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.sanchit.funda.model.MutualFund;

@Entity (tableName = "mutual_funds")
public class MutualFundModel {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo
    public String amfiID;

    @ColumnInfo
    public String fundName;

    @ColumnInfo
    public String fundHouse;

    @ColumnInfo
    public String category;

    @ColumnInfo
    public String subCategory;

    @ColumnInfo
    public String appDefinedCategory;

    @ColumnInfo
    public String isin;

    @ColumnInfo
    public String benchmark;

    @ColumnInfo
    public String aaum;

    @ColumnInfo
    public String fundManager;

    @ColumnInfo
    public String exitLoad;

    @ColumnInfo
    public String fundAim;

    @ColumnInfo
    public boolean isDirect;

    @Ignore
    public MutualFundModel(MutualFund fund) {
        this.amfiID = fund.getAmfiID();
        this.fundName = fund.getFundName();
        this.fundHouse = fund.getFundHouse();
        this.category = fund.getCategory();
        this.subCategory = fund.getSubCategory();
        this.appDefinedCategory = fund.getAppDefinedCategory();
        this.isin = fund.getIsin();
        this.benchmark = fund.getBenchmark();
        this.aaum = fund.getAaum();
        this.fundManager = fund.getFundManager();
        this.exitLoad = fund.getExitLoad();
        this.fundAim = fund.getFundAim();
        this.isDirect = fund.isDirect();
    }

    public MutualFundModel(String amfiID, String fundName, String fundHouse, String category, String subCategory, String appDefinedCategory, String isin, String benchmark, String aaum, String fundManager, String exitLoad, String fundAim, boolean isDirect) {
        this.amfiID = amfiID;
        this.fundName = fundName;
        this.fundHouse = fundHouse;
        this.category = category;
        this.subCategory = subCategory;
        this.appDefinedCategory = appDefinedCategory;
        this.isin = isin;
        this.benchmark = benchmark;
        this.aaum = aaum;
        this.fundManager = fundManager;
        this.exitLoad = exitLoad;
        this.fundAim = fundAim;
        this.isDirect = isDirect;
    }

    public MutualFundModel() {}
}
