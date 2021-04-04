package com.sanchit.funda.dao.task;

import android.os.AsyncTask;

import com.sanchit.funda.dao.AbstractDao;
import com.sanchit.funda.log.LogManager;

public class DeleteActionTask<T extends AbstractDao<V>, V> extends AsyncTask<Void, Void, Void> {

    private final T dao;
    private final V item;

    private long start;

    public DeleteActionTask(T dao, V item) {
        this.dao = dao;
        this.item = item;
        start = System.currentTimeMillis();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        dao.delete(item);
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