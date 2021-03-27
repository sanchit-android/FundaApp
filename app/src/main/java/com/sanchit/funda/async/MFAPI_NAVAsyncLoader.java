package com.sanchit.funda.async;

import android.app.Activity;

import com.sanchit.funda.async.event.OnEnrichmentCompleted;
import com.sanchit.funda.async.task.ChainedTask;
import com.sanchit.funda.content.rest.MFAPI_NAVEnricher;
import com.sanchit.funda.model.MFPriceModel;

public class MFAPI_NAVAsyncLoader extends ChainedTask<String, Void, MFPriceModel> {

    private final EnrichmentModel enrichmentModel;

    public MFAPI_NAVAsyncLoader(Activity activity, OnEnrichmentCompleted<MFPriceModel> callback) {
        super(activity, callback);
        this.enrichmentModel = EnrichmentModel.Default;
    }

    public MFAPI_NAVAsyncLoader(Activity activity, OnEnrichmentCompleted<MFPriceModel> callback,
                                EnrichmentModel enrichmentModel) {
        super(activity, callback);
        this.enrichmentModel = enrichmentModel;
    }

    @Override
    protected MFPriceModel doInBackground(String... args) {
        return new MFAPI_NAVEnricher(enrichmentModel).enrich(args[0]);
    }

    public enum EnrichmentModel {
        Default;
    }
}