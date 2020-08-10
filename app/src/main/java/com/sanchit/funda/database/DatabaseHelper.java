package com.sanchit.funda.database;

import com.sanchit.funda.dao.AbstractDao;
import com.sanchit.funda.dao.task.InsertActionTask;
import com.sanchit.funda.dao.task.SelectActionTask;

public class DatabaseHelper {

    public static <T extends AbstractDao<V>, V> void insert(T dao, V... items) {
        new InsertActionTask<T, V>(dao, items).execute();
    }

    public static <T extends AbstractDao<V>, V> void selectAll(T dao, SelectActionTask.SelectActionCallback<V> callback) {
        new SelectActionTask<T, V>(dao, callback).execute();
    }

}
