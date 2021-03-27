package com.sanchit.funda.model.homesummary;

public abstract class AbstractHomeSummary2Model {

    protected String header;
    protected String content;
    protected  String detail;

    public AbstractHomeSummary2Model(String header) {
        this.header = header;
    }

    protected abstract void initModel();

    public String getHeader() {
        return header;
    }

    public String getContent() {
        return content;
    }

    public String getDetail() {
        return detail;
    }
}
