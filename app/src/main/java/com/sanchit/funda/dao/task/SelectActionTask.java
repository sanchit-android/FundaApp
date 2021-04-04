package com.sanchit.funda.dao.task;

import android.os.AsyncTask;

import com.sanchit.funda.dao.AbstractDao;
import com.sanchit.funda.log.LogManager;

import java.util.List;

public class SelectActionTask<T extends AbstractDao<V>, V> extends AsyncTask<Void, Void, List<V>> {

    private final T dao;
    private final SelectActionCallback<V> callback;

    private long start;

    public SelectActionTask(T dao, SelectActionCallback<V> callback) {
        this.dao = dao;
        this.callback = callback;
        start = System.currentTimeMillis();
    }

    @Override
    protected List<V> doInBackground(Void... voids) {
        return dao.selectAll();
    }

    protected void onPostExecute(List<V> noData) {
        callback.onSelectComplete(noData);

        long duration = System.currentTimeMillis() - start;
        LogManager.log(getName() + " async task: " + duration + " msec.");
    }

    public interface SelectActionCallback<V> {
        void onSelectComplete(List<V> resultSet);
    }

    public String getName() {
        return this.getClass().getSimpleName() + "|" + dao.getClass().getSimpleName();
    }
}

