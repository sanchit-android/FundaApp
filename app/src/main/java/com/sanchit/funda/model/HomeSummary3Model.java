package com.sanchit.funda.model;

public class HomeSummary2Model {

    private String header;
    private String content;
    private String subContent;

    public HomeSummary2Model() {
    }

    public HomeSummary2Model(String header, String content, String subContent) {
        this.header = header;
        this.content = content;
        this.subContent = subContent;
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

    public String getSubContent() {
        return subContent;
    }

    public void setSubContent(String subContent) {
        this.subContent = subContent;
    }
}
