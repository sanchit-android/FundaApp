package com.sanchit.funda.async;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;

import com.sanchit.funda.async.event.OnEnrichmentCompleted;
import com.sanchit.funda.content.FundsRawDataParser;
import com.sanchit.funda.model.MutualFund;

import java.io.IOException;
import java.util.List;

public class FundsRawDataAsyncLoader extends AsyncTask<Uri, Void, List<MutualFund>> {

    private final Activity activity;
    private final OnEnrichmentCompleted<List<MutualFund>> callback;

    public FundsRawDataAsyncLoader(Activity activity, OnEnrichmentCompleted<List<MutualFund>> callback) {
        this.activity = activity;
        this.callback = callback;
    }

    @Override
    protected List<MutualFund> doInBackground(Uri... args) {
        try {
            return new FundsRawDataParser().parse(activity, null);
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<MutualFund> data) {
        callback.updateView(data);
    }
}
