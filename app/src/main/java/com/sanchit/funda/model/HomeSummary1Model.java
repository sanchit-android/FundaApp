package com.sanchit.funda.model;

public class HomeSummary1Model {

    private String header;
    private String content;

    public HomeSummary1Model() {
    }

    public HomeSummary1Model(String header, String content) {
        this.header = header;
        this.content = content;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
