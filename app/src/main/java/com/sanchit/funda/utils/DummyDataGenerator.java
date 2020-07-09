package com.sanchit.funda.utils;

import com.sanchit.funda.model.HomeSummary1Model;
import com.sanchit.funda.model.HomeSummary2Model;
import com.sanchit.funda.model.HomeSummary3Model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class DummyDataGenerator {

    public static List<HomeSummary1Model> generateHomeSumary1Model() {
        List<HomeSummary1Model> model = new ArrayList<>();
        model.add(new HomeSummary1Model("Top Fund", "Axis Bluechip Fund"));
        model.add(new HomeSummary1Model("Top Category", "FoF Overses - Emerging Markets"));
        model.add(new HomeSummary1Model("Least Performing Fund", "L&T Emerging Business Fund"));
        return model;
    }

    public static List<HomeSummary2Model> generateHomeSumary2Model() {
        List<HomeSummary2Model> model = new ArrayList<>();
        model.add(new HomeSummary2Model("Equity Debt Ratio", "9 : 1", "Equity Aggressive"));
        model.add(new HomeSummary2Model("Large : Mid : Small Cap Ratio", "5 : 3 : 2", "Balanced on Cap"));
        model.add(new HomeSummary2Model("Overweight Fund", "15% (> 10%)", "Axis Bluechip Fund"));
        return model;
    }

    public static List<HomeSummary3Model> generateHomeSumary3Model() {
        List<HomeSummary3Model> model = new ArrayList<>();
        model.add(new HomeSummary3Model("Nifty 50", new BigDecimal("10799.56"), new BigDecimal("0.0033")));
        model.add(new HomeSummary3Model("NIFTY Free Float Midcap", new BigDecimal("15799.56"), new BigDecimal("-0.0044")));
        model.add(new HomeSummary3Model("NIFTY Free Float Smallcap", new BigDecimal("27099.56"), new BigDecimal("-0.0125")));
        return model;
    }
}
