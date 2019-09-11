package com.example.restaurants.Fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.restaurants.Model.Model;
import com.example.restaurants.Model.Post;
import com.example.restaurants.Model.PostAdapter;
import com.example.restaurants.Model.PostsLinkedList;
import com.example.segev.restaurants.R;

import java.util.LinkedList;

import static android.support.v7.widget.DividerItemDecoration.VERTICAL;

public class SearchFragment extends Fragment implements PostAdapter.ItemClickListener{
    public static final String LOG_TAG = SearchFragment.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private PostAdapter mAdapter;

    LinkedList<Post> postsList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        postsList = (PostsLinkedList<Post>) getArguments().getSerializable("PostsList");



        View rootView = inflater.inflate(R.layout.fragment_search, container, false);

        final TextView emptyListMessage = rootView.findViewById(R.id.search_no_posts);
        if(postsList.isEmpty()){
            emptyListMessage.setVisibility(View.VISIBLE);
        }else{
            emptyListMessage.setVisibility(View.GONE);
        }

        // Set the RecyclerView to its corresponding view
        mRecyclerView = rootView.findViewById(R.id.search_recyclerView);

        // Set the layout for the RecyclerView to be a linear layout, which measures and
        // positions items within a RecyclerView into a linear list
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));

        // Initialize the adapter and attach it to the RecyclerView
        mAdapter = new PostAdapter(getActivity(), this);
        mAdapter.setPosts(postsList);
        mRecyclerView.setAdapter(mAdapter);

        DividerItemDecoration decoration = new DividerItemDecoration(getActivity().getApplicationContext(), VERTICAL);
        mRecyclerView.addItemDecoration(decoration);

        return rootView;
    }

    // Gets the post from the local DB and opening a PostDetailsFragment with it.
    @Override
    public void onItemClickListener(int itemId) { // check if a spinner isn needed
        final ProgressDialog dialog = ProgressDialog.show(getActivity(), "",
                "Please wait...", true);
        Model.getInstance().getPostById(itemId, new SavedFragment.onGotPostById() {
            @Override
            public void onComplete(Post post) {
                Fragment fragment = new PostDetailsFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("Post",post);
                fragment.setArguments(bundle);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                dialog.dismiss();
                fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Search Results");
    }
}


