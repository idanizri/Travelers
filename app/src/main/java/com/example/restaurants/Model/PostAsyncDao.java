package com.example.restaurants.Model;

import android.os.AsyncTask;

import com.example.restaurants.MyApplication;

import java.util.List;

public class PostAsyncDao {

    public interface PostAsyncDaoListener<T>{
        void onComplete(T data);
    }

    public static void getAllPosts(final PostAsyncDaoListener<List<Post>> listener) {
        class MyAsyncTask extends AsyncTask<String,String,List<Post>>{
            @Override
            protected List<Post> doInBackground(String... strings) {
                return AppLocalDb.getsInstance(MyApplication.context).postDao().getAllPosts();
            }

            @Override
            protected void onPostExecute(List<Post> posts) {
                super.onPostExecute(posts);
                listener.onComplete(posts);
            }
        }
        MyAsyncTask task = new MyAsyncTask();
        task.execute();
    }


    public static void insertAllPosts(final PostAsyncDaoListener<Boolean> listener,List<Post> posts){
        class MyAsyncTask extends AsyncTask<List<Post>,String,Boolean>{
            @Override
            protected Boolean doInBackground(List<Post>... posts) {
                for (Post post : posts[0]) {
                    AppLocalDb.getsInstance(MyApplication.context).postDao().insertPost(post);
                }
                return true;
            }

            @Override
            protected void onPostExecute(Boolean success) {
                super.onPostExecute(success);
                listener.onComplete(success);
            }
        }
        MyAsyncTask task = new MyAsyncTask();
        task.execute(posts);
    }

    public static void deleteAllPosts(final PostAsyncDaoListener<Boolean> listener,final List<Post> postsToDelete){
        class MyAsyncTask extends AsyncTask<List<Post>,String,Boolean>{
            @Override
            protected Boolean doInBackground(List<Post>... posts) {
                for(Post post : postsToDelete){
                    AppLocalDb.getsInstance(MyApplication.context).postDao().deletePost(post);
                }
                return true;
            }

            @Override
            protected void onPostExecute(Boolean success) {
                super.onPostExecute(success);
                listener.onComplete(success);
            }
        }
        MyAsyncTask task = new MyAsyncTask();
        task.execute();
    }

    static public void getPostById(final PostAsyncDaoListener<Post> listener, final int id) {
        class MyAsyncTask extends AsyncTask<String,String,Post>{
            @Override
            protected Post doInBackground(String... strings) {
                return AppLocalDb.getsInstance(MyApplication.context).postDao().getPostByID(id);
            }

            @Override
            protected void onPostExecute(Post post) {
                super.onPostExecute(post);
                listener.onComplete(post);
            }
        }
        MyAsyncTask task = new MyAsyncTask();
        task.execute();
    }

    static public void getPostByLocation(final PostAsyncDaoListener<List<Post>> listener, final String location) {
        class MyAsyncTask extends AsyncTask<String,String,List<Post>>{
            @Override
            protected List<Post> doInBackground(String... strings) {
                return AppLocalDb.getsInstance(MyApplication.context).postDao().getPostByLocation(location);
            }

            @Override
            protected void onPostExecute(List<Post> post) {
                super.onPostExecute(post);
                listener.onComplete(post);
            }
        }
        MyAsyncTask task = new MyAsyncTask();
        task.execute();
    }

    static public void deletePost(final PostAsyncDaoListener<Post> listener, final Post postToDelete) {
        class MyAsyncTask extends AsyncTask<String,String,Boolean>{
            @Override
            protected Boolean doInBackground(String... strings) {
                AppLocalDb.getsInstance(MyApplication.context).postDao().deletePost(postToDelete);
                return true;
            }

            @Override
            protected void onPostExecute(Boolean bool) {
                super.onPostExecute(bool);
                listener.onComplete(null);
            }
        }
        MyAsyncTask task = new MyAsyncTask();
        task.execute();
    }
}
