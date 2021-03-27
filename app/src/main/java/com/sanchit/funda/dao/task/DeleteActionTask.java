package com.sanchit.funda.dao.task;

import android.os.AsyncTask;

import com.sanchit.funda.dao.AbstractDao;

public class DeleteActionTask<T extends AbstractDao<V>, V> extends AsyncTask<Void, Void, Void> {

    private final T dao;
    private final V item;

    public DeleteActionTask(T dao, V item) {
        this.dao = dao;
        this.item = item;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        dao.delete(item);
        return null;
    }

    protected void onPostExecute(Void noData) {
        // nothing
    }
}