package com.sanchit.funda.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.sanchit.funda.dao.UserDataDao;
import com.sanchit.funda.dao.entity.UserDataModel;

@Database(entities = {UserDataModel.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase db;

    public static AppDatabase getInstance(Context context) {
        if (db != null) {
            return db;
        }

        synchronized (AppDatabase.class) {
            if (db == null) {
                db = Room.databaseBuilder(context, AppDatabase.class, "funda")
                        .fallbackToDestructiveMigration().build();
            }
            return db;
        }
    }

    public abstract UserDataDao userDataDao();
}
