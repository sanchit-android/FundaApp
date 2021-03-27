package com.sanchit.funda.cashflow;

import com.sanchit.funda.model.MFTrade;
import com.sanchit.funda.model.cashflow.CashflowPosition;
import com.sanchit.funda.model.cashflow.ClosedTaxlotGroup;
import com.sanchit.funda.model.cashflow.Taxlot;
import com.sanchit.funda.model.cashflow.UnclosedTaxlotGroup;
import com.sanchit.funda.utils.Constants;
import com.sanchit.funda.utils.NumberUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FIFOCashflowEngine {

    public static final Comparator<MFTrade> TRADE_COMPARATOR = (o1, o2) -> {
        // sort by investment date & for a buy/sell on same date, put sell first then buy
        if (!o1.getInvestmentDate().equals(o2.getInvestmentDate())) {
            return o1.getInvestmentDate().compareTo(o2.getInvestmentDate());
        }
        return o2.getSide().compareTo(o1.getSide());
    };

    public List<CashflowPosition> process(List<MFTrade> trades) {
        Map<String, List<MFTrade>> groups = groupTrades(trades);
        List<CashflowPosition> cashflows = new ArrayList<>();

        for (Map.Entry<String, List<MFTrade>> entry : groups.entrySet()) {
            if (entry.getKey().equals("118551")) {
                System.out.println("");
            }
            CashflowPosition cashflowPosition = generateCashflow(entry);
            cashflows.add(cashflowPosition);
        }
        return cashflows;
    }

    private CashflowPosition generateCashflow(Map.Entry<String, List<MFTrade>> entry) {
        CashflowPosition cashflowPosition = new CashflowPosition();
        String amfiId = entry.getKey();
        List<MFTrade> trades = entry.getValue();
        cashflowPosition.setFund(trades.get(0).getFund());

        Collections.sort(trades, TRADE_COMPARATOR);

        List<Taxlot> runningOpenLots = new ArrayList<>();
        List<Taxlot> runningCloseLots = new ArrayList<>();
        BigDecimal runningOpen = BigDecimal.ZERO;
        BigDecimal runningClose = BigDecimal.ZERO;
        BigDecimal runningOpenCost = BigDecimal.ZERO;
        BigDecimal runningCloseCost = BigDecimal.ZERO;
        for (MFTrade trade : trades) {
            Taxlot taxlot = Taxlot.from(trade);

            if (Constants.Side.BUY.equals(taxlot.getSide())) {
                runningOpenLots.add(taxlot);
                runningOpen = runningOpen.add(taxlot.getQuantity());
                runningOpenCost = runningOpenCost.add(taxlot.getCost());
            } else if (Constants.Side.SELL.equals(taxlot.getSide())) {
                BigDecimal closeQuantity = taxlot.getQuantity();
                runningCloseLots.add(taxlot);
                runningClose = runningClose.add(closeQuantity);
                runningCloseCost = runningCloseCost.add(taxlot.getCost());

                int index = perfectClosingAtLot(closeQuantity, runningOpen, runningClose, runningOpenLots, runningCloseLots);
                if (index > -1) {
                    ClosedTaxlotGroup group = new ClosedTaxlotGroup();

                    BigDecimal openingCost = BigDecimal.ZERO;
                    for (int i = 0; i <= index; i++) {
                        group.getOpeningLots().add(runningOpenLots.get(i));
                        openingCost = openingCost.add(runningOpenLots.get(i).getCost());
                    }

                    group.getClosingLots().addAll(runningCloseLots);
                    group.setQuantity(runningClose);
                    group.setOpeningCost(openingCost);
                    group.setClosingCost(runningCloseCost);
                    group.setRealizedPNL(runningCloseCost.subtract(openingCost));

                    cashflowPosition.getRealizedTaxlots().add(group);

                    runningOpenLots = runningOpenLots.subList(index + 1, runningOpenLots.size());
                    runningCloseLots.clear();
                    runningClose = BigDecimal.ZERO;
                    runningCloseCost = BigDecimal.ZERO;
                    runningOpenCost = runningOpenCost.subtract(openingCost);
                    runningOpen = runningOpenLots.stream().map(t -> t.getQuantity()).reduce(BigDecimal.ZERO, BigDecimal::add);
                }
            }
        }

        UnclosedTaxlotGroup unclosedTaxlotGroup = new UnclosedTaxlotGroup();
        cashflowPosition.setUnclosedTaxlotGroup(unclosedTaxlotGroup);
        if (runningOpenLots.size() > 0) {
            unclosedTaxlotGroup.getOpeningLots().addAll(runningOpenLots);
            unclosedTaxlotGroup.getClosingLots().addAll(runningCloseLots);
            unclosedTaxlotGroup.setOpeningCost(runningOpenCost);
            unclosedTaxlotGroup.setClosingCost(runningCloseCost);
            unclosedTaxlotGroup.setRealizedQuantity(runningClose);
            unclosedTaxlotGroup.setUnrealizedQuantity(runningOpen.subtract(runningClose));

            BigDecimal toClose = new BigDecimal(runningClose.toPlainString());
            BigDecimal matchingOpenCost = BigDecimal.ZERO;
            BigDecimal unrealizedInvestmentCost = BigDecimal.ZERO;
            boolean calculateUnrealizedInvestmentCost = false;
            for (Taxlot t : runningOpenLots) {
                if (calculateUnrealizedInvestmentCost) {
                    unrealizedInvestmentCost = unrealizedInvestmentCost.add(t.getCost());
                    continue;
                }

                if (t.getQuantity().compareTo(toClose) >= 0 || NumberUtils.equals(t.getQuantity(), toClose)) {
                    matchingOpenCost = matchingOpenCost.add(toClose.multiply(t.getCostPrice()));
                    unrealizedInvestmentCost = unrealizedInvestmentCost.add(t.getQuantity().subtract(toClose).multiply(t.getCostPrice()));
                    calculateUnrealizedInvestmentCost = true;
                } else {
                    matchingOpenCost = matchingOpenCost.add(t.getCost());
                    toClose = toClose.subtract(t.getQuantity());
                }
            }
            unclosedTaxlotGroup.setRealizedPNL(runningCloseCost.subtract(matchingOpenCost));
            unclosedTaxlotGroup.setCostOfUnrealizedInvestments(unrealizedInvestmentCost);
        }

        return cashflowPosition;
    }

    private int perfectClosingAtLot(BigDecimal closeQuantity, BigDecimal runningOpen, BigDecimal runningClose, List<Taxlot> runningOpenLots, List<Taxlot> runningCloseLots) {
        // Full Unwind
        if (NumberUtils.equals(runningClose, runningOpen)) {
            return runningOpenLots.size() - 1;
        }
        // Impossible; Cant unwind more than Open
        if (runningClose.compareTo(runningOpen) > 0) {
            return -1;
        }
        BigDecimal openUntilNow = BigDecimal.ZERO;
        for (int i = 0; i < runningOpenLots.size(); i++) {
            openUntilNow = openUntilNow.add(runningOpenLots.get(i).getQuantity());
            if (NumberUtils.equals(runningClose, openUntilNow)) {
                return runningOpenLots.size() - 1;
            }
        }
        return -1;
    }

    private Map<String, List<MFTrade>> groupTrades(List<MFTrade> trades) {
        Map<String, List<MFTrade>> groups = new HashMap<>();
        for (MFTrade trade : trades) {
            if (!groups.containsKey(trade.getFund().getAmfiID())) {
                groups.put(trade.getFund().getAmfiID(), new ArrayList<>());
            }
            groups.get(trade.getFund().getAmfiID()).add(trade);
        }
        return groups;
    }

}
