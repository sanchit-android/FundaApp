package com.sanchit.funda.model;

public class HomeSummary2Model {

    private String header;
    private String content;

    public HomeSummary2Model() {
    }

    public HomeSummary2Model(String header, String content) {
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
