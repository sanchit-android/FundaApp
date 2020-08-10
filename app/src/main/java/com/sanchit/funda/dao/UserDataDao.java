package com.sanchit.funda.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.sanchit.funda.dao.entity.UserDataModel;

import java.util.List;

@Dao
public interface UserDataDao extends AbstractDao<UserDataModel> {

    @Query("SELECT * FROM user_data_model")
    List<UserDataModel> selectAll();

    @Insert
    void insertAll(UserDataModel... users);

    @Delete
    void delete(UserDataModel user);


}
