package com.sanchit.funda.async.task;

import android.app.Activity;
import android.os.AsyncTask;

import com.sanchit.funda.async.event.OnEnrichmentCompleted;

public abstract class ChainedTask<U, V, W> extends AsyncTask<U, V, W> implements Chainable {

    protected final Activity activity;
    protected final OnEnrichmentCompleted<W> callback;

    protected ChainedTask next;

    public ChainedTask(Activity activity, OnEnrichmentCompleted<W> callback) {
        this.activity = activity;
        this.callback = callback;
    }

    @Override
    protected void onPostExecute(W data) {
        callback.updateView(data);
        if (next != null) {
            next.execute();
        }
    }

    @Override
    public ChainedTask chain(ChainedTask task) {
        next = task;
        return next;
    }
}
