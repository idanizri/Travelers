package com.example.restaurants.Model;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface PostDao {

    @Query("SELECT * FROM Post")
    List<Post> getAllPosts();

    @Query("SELECT * FROM Post WHERE id = :id")
    Post getPostByID(int id);

    @Query("SELECT * FROM Post WHERE LOWER(location) = LOWER(:location)")
    List<Post> getPostByLocation(String location);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPost(Post post);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updatePost(Post post);

    @Delete
    void deletePost(Post post);
}
