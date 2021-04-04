package com.sanchit.funda.async.task;

import android.app.Activity;
import android.os.AsyncTask;

import com.sanchit.funda.async.event.OnEnrichmentCompleted;
import com.sanchit.funda.log.LogManager;

public abstract class ChainedTask<U, V, W> extends AsyncTask<U, V, W> implements Chainable {

    protected final Activity activity;
    protected final OnEnrichmentCompleted<W> callback;

    protected ChainedTask next;

    private long start;

    public ChainedTask(Activity activity, OnEnrichmentCompleted<W> callback) {
        this.activity = activity;
        this.callback = callback;
        start = System.currentTimeMillis();
    }

    @Override
    protected void onPostExecute(W data) {
        callback.updateView(data);

        long duration = System.currentTimeMillis() - start;
        LogManager.log(getName() + " async task: " + duration + " msec.");

        if (next != null) {
            next.execute();
        }
    }

    @Override
    public ChainedTask chain(ChainedTask task) {
        next = task;
        return next;
    }

    public abstract String getName();
}
