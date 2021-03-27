package com.sanchit.funda.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

public abstract class AbstractMFObject implements Serializable {

    private String UID;

    private MutualFund fund;
    private BigDecimal quantity;
    private BigDecimal costPrice;
    private BigDecimal cost;

    public MutualFund getFund() {
        return fund;
    }

    public void setFund(MutualFund fund) {
        this.fund = fund;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(BigDecimal costPrice) {
        this.costPrice = costPrice;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractMFObject that = (AbstractMFObject) o;
        return Objects.equals(UID, that.UID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(UID);
    }
}
