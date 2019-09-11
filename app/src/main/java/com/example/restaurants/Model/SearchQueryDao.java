package com.example.restaurants.Model;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface SearchQueryDao {

    @Query("SELECT * FROM SearchQuery")
    List<SearchQuery> getAllSearchQueries();

    @Query("SELECT * FROM SearchQuery WHERE `query` = :query")
    SearchQuery getSearchQueryByLocation(String query);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSearchQuery(SearchQuery searchQuery);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateSearchQuery(SearchQuery searchQuery);

    @Delete
    void deleteSearchQuery(SearchQuery searchQuery);

    //implement drop table
}
