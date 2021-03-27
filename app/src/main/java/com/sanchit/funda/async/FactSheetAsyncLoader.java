package com.sanchit.funda.async;

import android.app.Activity;

import com.sanchit.funda.async.event.OnEnrichmentCompleted;
import com.sanchit.funda.async.task.ChainedTask;
import com.sanchit.funda.content.file.factsheet.AbstractFactSheetParser;

public class FactSheetAsyncLoader extends ChainedTask<Void, String, Void> {

    private final AbstractFactSheetParser parser;

    public FactSheetAsyncLoader(Activity activity, OnEnrichmentCompleted<Void> callback, AbstractFactSheetParser parser) {
        super(activity, callback);
        this.parser = parser;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            parser.parse(activity, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
