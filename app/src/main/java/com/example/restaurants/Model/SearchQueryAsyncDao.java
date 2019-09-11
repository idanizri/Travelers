package com.example.restaurants.Model;

import android.os.AsyncTask;
import android.util.Log;

import com.example.restaurants.MyApplication;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class SearchQueryAsyncDao {

    private static final String LOG_TAG = SearchQueryAsyncDao.class.getSimpleName();

    public interface SearchQueryAsyncDaoListener<T>{
        void onComplete(T data);
    }

    public static void getAllSearchQueries(final SearchQueryAsyncDaoListener<List<SearchQuery>> listener) {
        class MyAsyncTask extends AsyncTask<String,String,List<SearchQuery>>{
            @Override
            protected List<SearchQuery> doInBackground(String... strings) {
                return AppLocalDb.getsInstance(MyApplication.context).searchQueryDao().getAllSearchQueries();
            }

            @Override
            protected void onPostExecute(List<SearchQuery> searchQueryList) {
                super.onPostExecute(searchQueryList);
                listener.onComplete(searchQueryList);
            }
        }
        MyAsyncTask task = new MyAsyncTask();
        task.execute();
    }

    public static void getTopThreeQueries(final SearchQueryAsyncDaoListener<List<SearchQuery>> listener) {
        class MyAsyncTask extends AsyncTask<String,String,List<SearchQuery>>{
            @Override
            protected List<SearchQuery> doInBackground(String... strings) {
                List<SearchQuery> queriesList = AppLocalDb.getsInstance(MyApplication.context).searchQueryDao().getAllSearchQueries();

                Collections.sort(queriesList, new Comparator<SearchQuery>() {
                    @Override
                    public int compare(SearchQuery o1, SearchQuery o2) {
                        return o2.getSearchesAmount() - o1.getSearchesAmount();
                    }
                });

                if(queriesList.size() > 3)
                    return queriesList.subList(0,3);
                else{
                    return (queriesList);
                }
            }

            @Override
            protected void onPostExecute(List<SearchQuery> queries) {
                super.onPostExecute(queries);
                listener.onComplete(queries);
            }
        }
        MyAsyncTask task = new MyAsyncTask();
        task.execute();
    }

    public static void insertAllSearchQueries(final SearchQueryAsyncDaoListener<Boolean> listener,List<SearchQuery> searchQueryList){
        class MyAsyncTask extends AsyncTask<List<SearchQuery>,String,Boolean>{
            @Override
            protected Boolean doInBackground(List<SearchQuery>... queries) {
                for (SearchQuery query : queries[0]) {
                    AppLocalDb.getsInstance(MyApplication.context).searchQueryDao().insertSearchQuery(query);
                }
                return true;
            }

            @Override
            protected void onPostExecute(Boolean success) {
                super.onPostExecute(success);
                listener.onComplete(success);
            }
        }
        Log.d(LOG_TAG,"size " + searchQueryList.size());
        MyAsyncTask task = new MyAsyncTask();
        task.execute(searchQueryList);
    }


    public static void insertSearchQuery(final SearchQueryAsyncDaoListener<Boolean> listener,SearchQuery searchQuery){
        class MyAsyncTask extends AsyncTask<List<SearchQuery>,String,Boolean>{
            @Override
            protected Boolean doInBackground(List<SearchQuery>... queries) {
                for (SearchQuery query : queries[0]) {
                    AppLocalDb.getsInstance(MyApplication.context).searchQueryDao().insertSearchQuery(query);
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
        LinkedList<SearchQuery> temp = new LinkedList<>();
        temp.add(searchQuery);
        task.execute(temp);
    }

    static public void getSearchQueryByQuery(final SearchQueryAsyncDaoListener<SearchQuery> listener, final String query) {
        class MyAsyncTask extends AsyncTask<String,String,SearchQuery>{
            @Override
            protected SearchQuery doInBackground(String... strings) {
                return AppLocalDb.getsInstance(MyApplication.context).searchQueryDao().getSearchQueryByLocation(query);
            }

            @Override
            protected void onPostExecute(SearchQuery searchQuery) {
                super.onPostExecute(searchQuery);
                listener.onComplete(searchQuery);
            }
        }
        MyAsyncTask task = new MyAsyncTask();
        task.execute();
    }

    public static void deleteAllSearchQueries(final SearchQueryAsyncDaoListener<Boolean> listener,final List<SearchQuery> queriesToDelete){
        class MyAsyncTask extends AsyncTask<List<SearchQuery>,String,Boolean>{
            @Override
            protected Boolean doInBackground(List<SearchQuery>... queries) {
                for(SearchQuery query : queriesToDelete){
                    AppLocalDb.getsInstance(MyApplication.context).searchQueryDao().deleteSearchQuery(query);
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
}
