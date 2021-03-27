package com.sanchit.funda.model.homesummary;

import com.sanchit.funda.model.MutualFund;

public abstract class AbstractHomeSummary1Model {

    protected String header;
    protected String content;

    public AbstractHomeSummary1Model(String header) {
        this.header = header;
    }

    protected abstract void initModel();

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getContent() {
        return content;
    }

    public interface KeyProvider {
        KeyProvider FundNameKey = fund -> fund.getFundName();
        KeyProvider CategoryKey = fund -> fund.getCategory();
        KeyProvider SubCategoryKey = fund -> fund.getSubCategory();
        KeyProvider CustomCategoryKey = fund -> fund.getAppDefinedCategory();

        String fetchKey(MutualFund fund);
    }
}
