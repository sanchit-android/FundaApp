package com.sanchit.funda.async;

import android.app.Activity;
import android.net.Uri;

import com.sanchit.funda.async.event.OnEnrichmentCompleted;
import com.sanchit.funda.async.task.ChainedTask;
import com.sanchit.funda.content.file.FundsRawDataParser;
import com.sanchit.funda.model.MutualFund;

import java.io.IOException;
import java.util.List;

public class FundsRawDataAsyncLoader extends ChainedTask<Uri, Void, List<MutualFund>> {

    public FundsRawDataAsyncLoader(Activity activity, OnEnrichmentCompleted<List<MutualFund>> callback) {
        super(activity, callback);
    }

    @Override
    protected List<MutualFund> doInBackground(Uri... args) {
        try {
            return new FundsRawDataParser().parse(activity, null);
        } catch (IOException e) {
            return null;
        }
    }
}
