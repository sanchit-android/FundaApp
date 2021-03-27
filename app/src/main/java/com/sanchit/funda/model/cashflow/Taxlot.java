package com.sanchit.funda.model.cashflow;

import com.sanchit.funda.model.AbstractMFObject;
import com.sanchit.funda.model.MFTrade;
import com.sanchit.funda.model.MutualFund;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class Taxlot extends AbstractMFObject implements Serializable {

    private Date investmentDate;
    private String side;

    public static Taxlot from(MFTrade trade) {
        Taxlot t = new Taxlot();
        t.setFund(trade.getFund());
        t.setQuantity(trade.getQuantity());
        t.setCostPrice(trade.getCostPrice());
        t.setCost(trade.getCost());
        t.setInvestmentDate(trade.getInvestmentDate());
        t.setSide(trade.getSide());
        return t;
    }

    public Date getInvestmentDate() {
        return investmentDate;
    }

    public void setInvestmentDate(Date investmentDate) {
        this.investmentDate = investmentDate;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }
}
