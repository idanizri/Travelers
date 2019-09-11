package com.example.restaurants.Model;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;


import java.io.Serializable;
import java.util.List;

public class PostListViewModel extends ViewModel implements Serializable{
    private LiveData<List<Post>> data;

    public LiveData<List<Post>> getData(){
        data = Model.getInstance().getAllPosts();
        return data;
    }
}
