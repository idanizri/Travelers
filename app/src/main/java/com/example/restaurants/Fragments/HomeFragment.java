package com.example.restaurants.Fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.restaurants.Model.Model;
import com.example.restaurants.Model.Post;
import com.example.restaurants.Model.PostListViewModel;
import com.example.restaurants.Model.PostsLinkedList;
import com.example.restaurants.Model.SearchListViewModel;
import com.example.restaurants.Model.SearchQuery;
import com.example.restaurants.Model.SearchQueryAsyncDao;
import com.example.restaurants.Model.ViewPagerAdapter;
import com.example.restaurants.Model.ZoomOutPageTransformer;
import com.example.segev.restaurants.R;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HomeFragment extends Fragment {
    private static final String LOG_TAG = HomeFragment.class.getSimpleName();

    PostListViewModel postsViewModel;
    SearchListViewModel mSearchListViewModel;

    private Button mSaved_Posts_Button;
    private Button mSearch_Posts_Button;

    private EditText mSearch_Posts_Text;

    //private ViewPager mPager;
    private ViewPagerAdapter mAdapter;

    private int mPage;

    private ArrayList<TextView> mRecentSearch;


    private Thread timerThread;

    /////////////////////////////////////// LifeCycle Overrides ///////////////////////////////////////

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        postsViewModel = ViewModelProviders.of(this).get(PostListViewModel.class);
        postsViewModel.getData().observe(this, new Observer<List<Post>>() {
            @Override
            public void onChanged(@Nullable List<Post> posts) {
                mAdapter.setPosts(posts);
               // mPager.setAdapter(mAdapter);
            }
        });

        mSearchListViewModel = ViewModelProviders.of(this).get(SearchListViewModel.class);
        mSearchListViewModel.getData().observe(this, new Observer<List<SearchQuery>>() {
            @Override
            public void onChanged(@Nullable List<SearchQuery> searchQueryList) {
                mRecentSearch.get(0).setText("");



                Collections.sort(searchQueryList, new Comparator<SearchQuery>() {
                    @Override
                    public int compare(SearchQuery o1, SearchQuery o2) {
                        return o2.getSearchesAmount() - o1.getSearchesAmount();
                    }
                });
                if(searchQueryList.size() > 3)
                    searchQueryList = searchQueryList.subList(0,3);

                    switch(searchQueryList.size()){
                     case 0:
                        mRecentSearch.get(0).setText("");
                        mRecentSearch.get(1).setText("");
                        mRecentSearch.get(2).setText("");
                        break;
                    case 1:
                        mRecentSearch.get(0).setText(searchQueryList.get(0).getQuery());
                        mRecentSearch.get(1).setText("");
                        mRecentSearch.get(2).setText("");
                        break;
                    case 2:
                        mRecentSearch.get(0).setText(searchQueryList.get(0).getQuery());
                        mRecentSearch.get(1).setText(searchQueryList.get(1).getQuery());
                        mRecentSearch.get(2).setText("");
                        break;
                    case 3:
                        mRecentSearch.get(0).setText(searchQueryList.get(0).getQuery());
                        mRecentSearch.get(1).setText(searchQueryList.get(1).getQuery());
                        mRecentSearch.get(2).setText(searchQueryList.get(2).getQuery());
                }
                setRecentSearchOnClickListener(mRecentSearch);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Home");
        NavigationView view = getActivity().findViewById(R.id.nav_view);
        view.getMenu().getItem(0).setChecked(true);
    }

    @Override
    public void onDestroyView() {
        //timerThread.interrupt();
        super.onDestroyView();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        initializeViews(rootView);

        //mPager = rootView.findViewById(R.id.home_viewpager);
        mAdapter = new ViewPagerAdapter((AppCompatActivity)getActivity());


        //mPager.setPageTransformer(true,new ZoomOutPageTransformer());

//        mSaved_Posts_Button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) { onSavedPostsButtonClicked(); }
//        });
        mSearch_Posts_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { onSearchPostsButtonClicked();
            }
        });
        //mPager.setAdapter(mAdapter);


        mPage = 0;
        //startViewPagerTime(4000);

        return rootView;
    }

    /////////////////////////////////////// LifeCycle Overrides ///////////////////////////////////////


    private void setRecentSearchOnClickListener(ArrayList<TextView> recentSearches){
        for(final TextView recent : recentSearches){
            if(!TextUtils.isEmpty(recent.getText()))
            recent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateLatestSearches(recent.getText().toString().toLowerCase());
                    Model.getInstance().getPostByLocation(recent.getText().toString(), new Model.onGetPostByLocation() {
                        @Override
                        public void onDone(List<Post> postsList) {
                            moveToSearchFragment(postsList);
                        }
                    });
                }
            });
        }
    }

    private void onSearchPostsButtonClicked() {
        if(TextUtils.isEmpty(mSearch_Posts_Text.getText().toString())){
            return;
        }
        updateLatestSearches(mSearch_Posts_Text.getText().toString().toLowerCase());

        Model.getInstance().getPostByLocation(mSearch_Posts_Text.getText().toString(), new Model.onGetPostByLocation() {
            @Override
            public void onDone(List<Post> postsList) {
                moveToSearchFragment(postsList);
            }
        });
    }


    private void moveToSearchFragment(List<Post> postsList){
        Fragment fragment = null;

        try {
            fragment = SearchFragment.class.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Bundle bundle = new Bundle();
        PostsLinkedList<Post> postsLinkedList = new PostsLinkedList();
        postsLinkedList.addAll(postsList);

        bundle.putSerializable("PostsList",postsLinkedList);
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().addToBackStack(null).replace(R.id.flContent, fragment).commit();
    }

    private void updateLatestSearches(final String searchQuery) {
        Model.getInstance().getSearchByQuery(searchQuery, new SearchQueryAsyncDao.SearchQueryAsyncDaoListener<SearchQuery>() {
            @Override
            public void onComplete(SearchQuery getSearchByQueryResult) {
                if(getSearchByQueryResult != null) {
                    getSearchByQueryResult.setSearchesAmount(getSearchByQueryResult.getSearchesAmount() + 1);
                    Model.getInstance().insertSearch(getSearchByQueryResult);
                }else{
                    SearchQuery newQuery = new SearchQuery(searchQuery.toLowerCase(), 0);
                    Model.getInstance().insertSearch(newQuery);
                }
            }
        });
    }

//    private void onSavedPostsButtonClicked() {
//        Fragment fragment = null;
//        try {
//            fragment = SavedFragment.class.newInstance();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//        fragmentManager.beginTransaction().addToBackStack(null).replace(R.id.flContent, fragment).commit();
//    }

    private void initializeViews(View rootView){
        mSearch_Posts_Button = rootView.findViewById(R.id.homeserach_button);
//        mSaved_Posts_Button = rootView.findViewById(R.id.home_saved);
        mSearch_Posts_Text = rootView.findViewById(R.id.home_search_textview);

        mRecentSearch = new ArrayList<>();


        TextView recentSearch1 = rootView.findViewById(R.id.recentSearch1);
        TextView recentSearch2 = rootView.findViewById(R.id.recentSearch2);
        TextView recentSearch3 = rootView.findViewById(R.id.recentSearch3);

        mRecentSearch.add(recentSearch1);
        mRecentSearch.add(recentSearch2);
        mRecentSearch.add(recentSearch3);
    }


    /*private void startViewPagerTime(final int delay){
        Runnable runnable = new Runnable() {
            public void run() {
                if (mPage == mAdapter.getCount()) {
                    mPage = 0;
                } else {
                    mPage = mPager.getCurrentItem()+ 1;
                }
                mPager.setCurrentItem(mPage, true);
                mPager.postDelayed(this,delay);
            }
        };


        timerThread = new Thread(runnable);
        timerThread.start();
    }
    */
}