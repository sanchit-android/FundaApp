package com.sanchit.funda.async.event.main_activity;

import com.sanchit.funda.MainActivity;
import com.sanchit.funda.async.event.OnEnrichmentCompleted;
import com.sanchit.funda.cache.CacheManager;
import com.sanchit.funda.cache.Caches;
import com.sanchit.funda.model.MFTrade;

import java.util.ArrayList;
import java.util.List;

public class OnGrowwStatementLoadedHandler extends AbstractMainActivityEventHandler  implements OnEnrichmentCompleted<List<MFTrade>> {

    public OnGrowwStatementLoadedHandler(MainActivity activity) {
        super(activity);
    }

    @Override
    public void updateView(List<MFTrade> data) {
        if (data != null) {
            activity.trades.addAll(data);
            CacheManager.registerRawData(Caches.TRADES, activity.trades);
            CacheManager.registerCache(Caches.TRADES_BY_AMFI_ID, toCache(activity.trades));
            initiatePriceUpdateRequests();
        }
    }

    private CacheManager.Cache<String, List<MFTrade>> toCache(List<MFTrade> trades) {
        CacheManager.Cache<String, List<MFTrade>> cache = new CacheManager.Cache<>();
        for (MFTrade t : trades) {
            if (!cache.exists(t.getFund().getAmfiID())) {
                cache.add(t.getFund().getAmfiID(), new ArrayList<>());
            }
            cache.get(t.getFund().getAmfiID()).add(t);
        }
        return cache;
    }
}