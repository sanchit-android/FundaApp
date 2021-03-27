package com.sanchit.funda.model.homesummary;

import java.util.ArrayList;
import java.util.List;

public class TopFundModel {

    private String fundName;
    private String returns;

    public static List<TopFundModel> blankModel(int count) {
        List<TopFundModel> response = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            TopFundModel model = new TopFundModel();
            model.setFundName("-");
            model.setReturns("-");
            response.add(model);
        }
        return response;
    }

    public String getFundName() {
        return fundName;
    }

    public void setFundName(String fundName) {
        this.fundName = fundName;
    }

    public String getReturns() {
        return returns;
    }

    public void setReturns(String returns) {
        this.returns = returns;
    }
}
