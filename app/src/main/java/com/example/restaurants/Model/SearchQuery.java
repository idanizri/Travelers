package com.example.restaurants.Model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class SearchQuery {
    public int searchesAmount;
    @PrimaryKey
    @NonNull
    public String query;

    public SearchQuery(){}

    @Ignore
    public SearchQuery(String query,int searchesAmount) {
        this.searchesAmount = searchesAmount;
        this.query = query;
    }

    public int getSearchesAmount() {
        return searchesAmount;
    }

    public void setSearchesAmount(int searchesAmount) {
        this.searchesAmount = searchesAmount;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
