package com.sanchit.funda.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.sanchit.funda.dao.entity.MutualFundModel;

import java.util.List;

@Dao
public interface MutualFundDao extends AbstractDao<MutualFundModel> {

    @Query("SELECT * FROM mutual_funds")
    List<MutualFundModel> selectAll();

    @Insert
    void insertAll(MutualFundModel... funds);

    @Delete
    void delete(MutualFundModel fund);

}
