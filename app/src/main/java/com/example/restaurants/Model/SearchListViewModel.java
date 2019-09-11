package com.example.restaurants.Model;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;


import java.io.Serializable;
import java.util.List;

public class SearchListViewModel extends ViewModel implements Serializable{
    private LiveData<List<SearchQuery>> data;

    public LiveData<List<SearchQuery>> getData(){
        data = Model.getInstance().getAllSearchQueries();
        return data;
    }
}
