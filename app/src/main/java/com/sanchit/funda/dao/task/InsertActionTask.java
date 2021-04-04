package com.sanchit.funda.dao.task;

import android.os.AsyncTask;

import com.sanchit.funda.dao.AbstractDao;
import com.sanchit.funda.log.LogManager;

public class InsertActionTask<T extends AbstractDao<V>, V> extends AsyncTask<Void, Void, Void> {

    private final T dao;
    private final V[] items;

    private long start;

    public InsertActionTask(T dao, V[] items) {
        this.dao = dao;
        this.items = items;
        start = System.currentTimeMillis();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        dao.insertAll(items);
        return null;
    }

    protected void onPostExecute(Void noData) {
        long duration = System.currentTimeMillis() - start;
        LogManager.log(getName() + " async task: " + duration + " msec.");
    }

    public String getName() {
        return this.getClass().getSimpleName() + "|" + dao.getClass().getSimpleName();
    }
}
