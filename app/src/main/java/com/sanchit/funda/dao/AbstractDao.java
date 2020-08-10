package com.sanchit.funda.dao;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.sanchit.funda.dao.entity.UserDataModel;

import java.util.List;

public interface AbstractDao<T> {

    List<T> selectAll();

    void insertAll(T... items);

    void delete(T item);

}
