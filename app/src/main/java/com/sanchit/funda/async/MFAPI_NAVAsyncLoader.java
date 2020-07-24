package com.sanchit.funda.async;

import android.app.Activity;
import android.os.AsyncTask;

import com.sanchit.funda.async.event.OnEnrichmentCompleted;
import com.sanchit.funda.content.rest.MFAPI_NAVEnricher;
import com.sanchit.funda.model.MFPriceModel;

public class MFAPI_NAVAsyncLoader extends AsyncTask<String, Void, MFPriceModel> {

    private final Activity activity;
    private final OnEnrichmentCompleted<MFPriceModel> callback;

    public MFAPI_NAVAsyncLoader(Activity activity, OnEnrichmentCompleted<MFPriceModel> callback) {
        this.activity = activity;
        this.callback = callback;
    }

    @Override
    protected MFPriceModel doInBackground(String... args) {
        return new MFAPI_NAVEnricher().enrich(args[0]);
    }

    @Override
    protected void onPostExecute(MFPriceModel data) {
        callback.updateView(data);
    }
}