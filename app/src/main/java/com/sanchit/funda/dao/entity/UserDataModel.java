package com.sanchit.funda.dao.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_data_model")
public class UserDataModel {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "pan")
    public String pan;

    @ColumnInfo(name = "uri_path")
    public String uri;

    public UserDataModel(String name, String pan, String uri) {
        this.name = name;
        this.pan = pan;
        this.uri = uri;
    }

    @Override
    public String toString() {
        return id + "(" + name + ", " + pan + ", " + uri + ")";
    }
}
