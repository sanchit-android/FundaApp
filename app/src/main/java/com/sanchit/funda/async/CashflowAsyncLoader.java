package com.sanchit.funda.async;

import android.app.Activity;

import com.sanchit.funda.async.event.OnEnrichmentCompleted;
import com.sanchit.funda.async.task.ChainedTask;
import com.sanchit.funda.cashflow.FIFOCashflowEngine;
import com.sanchit.funda.model.MFTrade;
import com.sanchit.funda.model.cashflow.CashflowPosition;

import java.util.List;

public class CashflowAsyncLoader extends ChainedTask<Void, Void, List<CashflowPosition>> {

    private final List<MFTrade> trades;

    public CashflowAsyncLoader(Activity activity, OnEnrichmentCompleted<List<CashflowPosition>> callback, List<MFTrade> trades) {
        super(activity, callback);
        this.trades = trades;
    }

    @Override
    protected List<CashflowPosition> doInBackground(Void... voids) {
        return new FIFOCashflowEngine().process(trades);
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }
}
