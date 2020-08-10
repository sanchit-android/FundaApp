package com.sanchit.funda.dao.task;

import android.os.AsyncTask;

import com.sanchit.funda.dao.AbstractDao;

public class InsertActionTask<T extends AbstractDao<V>, V> extends AsyncTask<Void, Void, Void> {

    private final T dao;
    private final V[] items;

    public InsertActionTask(T dao, V[] items) {
        this.dao = dao;
        this.items = items;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        dao.insertAll(items);
        return null;
    }

    protected void onPostExecute(Void noData) {
        // nothing
    }
}
