package com.sanchit.funda.async.event.main_activity;

import com.sanchit.funda.MainActivity;
import com.sanchit.funda.async.MFAPI_NAVAsyncLoader;
import com.sanchit.funda.async.event.OnEnrichmentCompleted;
import com.sanchit.funda.cache.CacheManager;
import com.sanchit.funda.cache.Caches;
import com.sanchit.funda.model.MFPosition;
import com.sanchit.funda.model.MFTrade;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OnECASFileLoadedHandler extends AbstractMainActivityEventHandler implements OnEnrichmentCompleted<List<MFPosition>> {

    public OnECASFileLoadedHandler(MainActivity activity) {
        super(activity);
    }

    @Override
    public void updateView(List<MFPosition> data) {
        if (data != null) {
            activity.positions.addAll(data);
            CacheManager.registerCache(Caches.POSITIONS, toCache(activity.positions));
            initiatePriceUpdateRequests();
        }
    }

    private CacheManager.Cache<String, MFPosition> toCache(List<MFPosition> data) {
        CacheManager.Cache<String, MFPosition> cache = new CacheManager.Cache<>();
        for (MFPosition pos : data) {
            cache.add(pos.getFund().getAmfiID(), pos);
        }
        return cache;
    }
}
