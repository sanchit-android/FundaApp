package com.sanchit.funda.async;

import android.app.Activity;
import android.net.Uri;

import com.sanchit.funda.async.event.OnEnrichmentCompleted;
import com.sanchit.funda.async.task.ChainedTask;
import com.sanchit.funda.content.file.GrowwStatementParser;
import com.sanchit.funda.model.MFTrade;

import java.util.List;

public class GrowwStatementAsyncLoader extends ChainedTask<Uri, Void, List<MFTrade>> {

    private final String PAN;

    public GrowwStatementAsyncLoader(Activity activity, OnEnrichmentCompleted<List<MFTrade>> callback, String PAN) {
        super(activity, callback);
        this.PAN = PAN;
    }

    @Override
    protected List<MFTrade> doInBackground(Uri... args) {
        try {
            return new GrowwStatementParser(PAN).parse(activity, args[0]);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }
}
