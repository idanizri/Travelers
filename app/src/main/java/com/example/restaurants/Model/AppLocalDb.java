package com.example.restaurants.Model;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.util.Log;

@Database(entities = {Post.class,SearchQuery.class}, version = 4,exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class AppLocalDb extends RoomDatabase {

    private static final String LOG_TAG = AppLocalDb.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "posts";

    private static AppLocalDb sInstance;

    public static AppLocalDb getsInstance(Context context){
        if(sInstance == null){
            synchronized (LOCK){
                Log.d(LOG_TAG,"Creating new database instance");
                sInstance = Room.databaseBuilder(context.getApplicationContext(),AppLocalDb.class,AppLocalDb.DATABASE_NAME)
                        .fallbackToDestructiveMigration()
                        .build();
            }
        }
        return sInstance;
    }
    public abstract SearchQueryDao searchQueryDao();
    public abstract PostDao postDao();
}