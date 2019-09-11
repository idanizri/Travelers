package com.example.restaurants.Model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.io.Serializable;

@Entity
public class Post implements Serializable{
    @PrimaryKey
    @NonNull
    private String id;

    @NonNull
    private String userWhoPostedID;

    private Long updatedAt;

    private String title;

    private String location;

    private String description;

    private String image;

    private String postingUserEmail;


    public Post(@NonNull String userWhoPostedID, Long updatedAt, String title, String location, String description, String image, String postingUserEmail) {
        this.userWhoPostedID = userWhoPostedID;
        this.updatedAt = updatedAt;
        this.title = title;
        this.location = location;
        this.description = description;
        this.image = image;
        this.postingUserEmail = postingUserEmail;
        this.id = getUniqueId();
    }

    @Ignore
    public Post(){}

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    @NonNull
    public String getUserWhoPostedID() {
        return userWhoPostedID;
    }

    public void setUserWhoPostedID(@NonNull String userWhoPostedID) {
        this.userWhoPostedID = userWhoPostedID;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    private String getUniqueId(){
        return ((title.hashCode() + location.hashCode() + Long.valueOf(System.currentTimeMillis())) % Integer.MAX_VALUE) + "";
    }

    public String getPostingUserEmail() {
        return postingUserEmail;
    }

    public void setPostingUserEmail(String postingUserEmail) {
        this.postingUserEmail = postingUserEmail;
    }
}
