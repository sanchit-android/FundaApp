package com.sanchit.funda.model.cashflow;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ClosedTaxlotGroup {
    private List<Taxlot> openingLots = new ArrayList<>();
    private List<Taxlot> closingLots = new ArrayList<>();

    private BigDecimal quantity;
    private BigDecimal openingCost;
    private BigDecimal closingCost;
    private BigDecimal realizedPNL;

    public List<Taxlot> getOpeningLots() {
        return openingLots;
    }

    public List<Taxlot> getClosingLots() {
        return closingLots;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getOpeningCost() {
        return openingCost;
    }

    public void setOpeningCost(BigDecimal openingCost) {
        this.openingCost = openingCost;
    }

    public BigDecimal getClosingCost() {
        return closingCost;
    }

    public void setClosingCost(BigDecimal closingCost) {
        this.closingCost = closingCost;
    }

    public BigDecimal getRealizedPNL() {
        return realizedPNL;
    }

    public void setRealizedPNL(BigDecimal realizedPNL) {
        this.realizedPNL = realizedPNL;
    }
}
