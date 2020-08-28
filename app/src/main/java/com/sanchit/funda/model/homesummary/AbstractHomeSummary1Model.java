package com.sanchit.funda.model.homesummary;

import com.sanchit.funda.model.MFPosition;

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
        KeyProvider FundNameKey = position -> position.getFund().getFundName();
        KeyProvider CategoryKey = position -> position.getFund().getCategory();
        KeyProvider SubCategoryKey = position -> position.getFund().getSubCategory();
        KeyProvider CustomCategoryKey = position -> position.getFund().getAppDefinedCategory();

        String fetchKey(MFPosition position);
    }
}
