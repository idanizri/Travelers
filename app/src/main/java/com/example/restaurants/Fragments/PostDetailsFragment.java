package com.example.restaurants.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.restaurants.Model.Model;
import com.example.restaurants.Model.Post;
import com.example.restaurants.Model.UserModel;
import com.example.segev.restaurants.R;

import java.util.ArrayList;
import java.util.Arrays;

//ADD A IF(SAVEDINSTANCE STATE == NULL) CHECK LIKE ELIAV DID

/**
 * Takes on savedInstanceState bundle a boolean that says if to present options menu or not
 */
public class PostDetailsFragment extends Fragment {
    private static final String LOG_TAG = PostDetailsFragment.class.getSimpleName();

    private Post mPost;

    private TextView mTitle_Field;
    private TextView mLocation_Field;
    private TextView mDescription_Field;
    private ImageView mImages_View;

    MenuItem savedOptionsButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView =  inflater.inflate(R.layout.fragment_post_details, container, false);

        mPost = (Post)getArguments().getSerializable("Post");

        initializeViews(rootView);

        mDescription_Field.setMovementMethod(new ScrollingMovementMethod());
        setHasOptionsMenu(true);

        return rootView;
    }

    private void updateSaveButton(MenuItem savedItemOptionsButton) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("savedPosts",Context.MODE_PRIVATE);
        String whatsInSharedPreferencesSoFar = sharedPreferences.getString("saved","");
        if(whatsInSharedPreferencesSoFar.contains(mPost.getId())){
            savedItemOptionsButton.setTitle("Unsave");
        }else{
            savedItemOptionsButton.setTitle("Save");
        }
    }


    private void initializeViews(View rootView){
        mTitle_Field = rootView.findViewById(R.id.post_details_title);
        mLocation_Field = rootView.findViewById(R.id.post_details_location);
        mDescription_Field = rootView.findViewById(R.id.post_details_description);
        mImages_View = rootView.findViewById(R.id.post_details_image);

        mTitle_Field.setText(mPost.getTitle());
        mLocation_Field.setText(mPost.getLocation());
        mDescription_Field.setText(mPost.getDescription());


        Model.getInstance().getImage(mPost.getImage(), new Model.GetImageListener() {
            @Override
            public void onDone(Bitmap imageBitmap) {
                mImages_View.setImageBitmap(imageBitmap);
            }
        });

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.post_options, menu);
        if(!(mPost.getUserWhoPostedID().equals(UserModel.getInstance().getCurrentUser().getUid()))){ // not owner of post
            menu.getItem(0).setVisible(false); // Edit
            menu.getItem(1).setVisible(false); // Delete
        }
        savedOptionsButton = menu.getItem(2);
        updateSaveButton(savedOptionsButton);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int ItemThatWasSelected = item.getItemId();

        switch(ItemThatWasSelected){
            case R.id.post_options_delete:
                onDeleteButtonClicked();
                return true;
            case R.id.post_options_edit:
                onEditButtonClicked();
                return true;

            case R.id.post_options_saved:
                onSaveButtonClicked();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onSaveButtonClicked() {
        //Getting what was so far in shared preferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("savedPosts",Context.MODE_PRIVATE);
        String whatsInSharedPreferencesSoFar = sharedPreferences.getString("saved","");
        StringBuilder resultToSave = new StringBuilder(whatsInSharedPreferencesSoFar);

        if(whatsInSharedPreferencesSoFar.contains(mPost.getId())){ // means the post is already saved.
            removeFromSharedPreferences(mPost.getId());
            Toast.makeText(getActivity(),"Unsaved",Toast.LENGTH_LONG);
            updateSaveButton(savedOptionsButton);
            return;
        }


        //Appending our value to it
        if(TextUtils.isEmpty(whatsInSharedPreferencesSoFar))
            resultToSave.append(mPost.getId());
        else
            resultToSave.append("," + mPost.getId());

        //Writing it to the shared preferences
        SharedPreferences.Editor sharedPreferencesEditor = getActivity().getSharedPreferences("savedPosts", Context.MODE_PRIVATE).edit();
        sharedPreferencesEditor.putString("saved",resultToSave.toString());
        sharedPreferencesEditor.apply();
        updateSaveButton(savedOptionsButton);
        Toast.makeText(getActivity(),"Saved",Toast.LENGTH_SHORT).show();
    }

    private void onDeleteButtonClicked(){
        Model.getInstance().deletePost(mPost);
        getActivity().getSupportFragmentManager().popBackStack();
    }

    private void onEditButtonClicked(){
        Fragment fragment = new PostEditFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("Post",mPost);
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().addToBackStack(null).replace(R.id.flContent, fragment).commit();
    }


    private void removeFromSharedPreferences(String idToRemove){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("savedPosts", Context.MODE_PRIVATE);
        String postsSaved = sharedPreferences.getString("saved","");

        ArrayList<String> postsToKeep = new ArrayList<>(Arrays.asList(postsSaved.split(","))); // arraylist of all saved
        postsToKeep.remove(idToRemove);

        String posts = TextUtils.join(",",postsToKeep);

        SharedPreferences.Editor sharedPreferencesEditor = getActivity().getSharedPreferences("savedPosts", Context.MODE_PRIVATE).edit();
        sharedPreferencesEditor.putString("saved",posts);
        sharedPreferencesEditor.apply();
    }


}
