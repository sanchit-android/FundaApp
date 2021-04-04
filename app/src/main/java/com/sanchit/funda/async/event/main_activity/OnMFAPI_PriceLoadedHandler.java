package com.sanchit.funda.async.event.main_activity;

import com.sanchit.funda.MainActivity;
import com.sanchit.funda.async.CashflowAsyncLoader;
import com.sanchit.funda.async.event.OnEnrichmentCompleted;
import com.sanchit.funda.cache.CacheManager;
import com.sanchit.funda.cache.Caches;
import com.sanchit.funda.model.MFPriceModel;

public class OnMFAPI_PriceLoadedHandler extends AbstractMainActivityEventHandler implements OnEnrichmentCompleted<MFPriceModel> {

    public OnMFAPI_PriceLoadedHandler(MainActivity activity) {
        super(activity);
    }

    @Override
    public void updateView(MFPriceModel data) {
        activity.priceMap.put(data.getAmfiID(), data);
        --activity.priceRequestsPending;

        CacheManager.getOrRegisterCache(Caches.PRICES_BY_AMFI_ID, MFPriceModel.class).add(data.getAmfiID(), data);

        if (activity.priceRequestsPending == 0) {
            new CashflowAsyncLoader(activity, new OnCashflowDataLoadedHandler(activity), activity.trades).execute();
        }
    }

}
