package com.sanchit.funda.async.event.main_activity;

import com.sanchit.funda.MainActivity;
import com.sanchit.funda.async.MFAPI_NAVAsyncLoader;
import com.sanchit.funda.cache.CacheManager;
import com.sanchit.funda.model.MFPosition;
import com.sanchit.funda.model.MFTrade;
import com.sanchit.funda.model.MutualFund;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AbstractMainActivityEventHandler {

    protected final MainActivity activity;

    public AbstractMainActivityEventHandler(MainActivity activity) {
        this.activity = activity;
    }

    protected void initiatePriceUpdateRequests() {
        Set<String> amfiIDs = new HashSet<>();
        for (MFPosition position : activity.positions) {
            String amfiID = position.getFund().getAmfiID();
            if (amfiIDs.contains(amfiID)) {
                continue;
            }

            new MFAPI_NAVAsyncLoader(activity, new OnMFAPI_PriceLoadedHandler(activity)).execute(amfiID);
            ++activity.priceRequestsPending;
            amfiIDs.add(amfiID);
        }
        for (MFTrade trade : activity.trades) {
            String amfiID = trade.getFund().getAmfiID();
            if (amfiIDs.contains(amfiID)) {
                continue;
            }

            new MFAPI_NAVAsyncLoader(activity, new OnMFAPI_PriceLoadedHandler(activity)).execute(amfiID);
            ++activity.priceRequestsPending;
            amfiIDs.add(amfiID);
        }
    }

    protected CacheManager.Cache<String, MutualFund> generateCacheByName(List<MutualFund> data) {
        CacheManager.Cache<String, MutualFund> cache = new CacheManager.Cache<>();
        for (MutualFund fund : data) {
            cache.add(fund.getFundName().toLowerCase(), fund);
        }
        return cache;
    }

    protected CacheManager.Cache<String, MutualFund> generateCacheByAmfiId(List<MutualFund> data) {
        CacheManager.Cache<String, MutualFund> cache = new CacheManager.Cache<>();
        for (MutualFund fund : data) {
            cache.add(fund.getAmfiID(), fund);
        }
        return cache;
    }

}
