package com.example.restaurants.Fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.restaurants.Model.DateConverter;
import com.example.restaurants.Model.Model;
import com.example.restaurants.Model.Post;
import com.example.segev.restaurants.R;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;



public class PostEditFragment extends Fragment {
    private static final String LOG_TAG = PostEditFragment.class.getSimpleName();
    private static final int GET_FROM_GALLERY = 3;

    private EditText mTitle_Field;
    private EditText mDescription_Field;
    private Button mUpload_Button;
    private EditText mLocation_Field;
    private Button mSubmit_Btn;

    private ImageView uploaded_imageView; // Presenting the currently uploaded image.

    Bitmap mPhotos;

    private Post mPost;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_edit_post, container, false);
        Bundle addedArguments = getArguments();

        if (addedArguments != null)  // Not supposed to happen, a post to edit should be transferred
            mPost = (Post) addedArguments.getSerializable("Post");
        else {
            mPost = new Post();
            Log.d(LOG_TAG, "addedArguments is null, check it out.");
        }
        initializeViews(rootView);

        mSubmit_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSubmitButtonClicked();
            }
        });
        mUpload_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onUploadImageButtonClicked();
            }
        });

        return rootView;
    }

    private void initializeViews(View rootView) {
        mTitle_Field = rootView.findViewById(R.id.post_edit_title);
        mLocation_Field = rootView.findViewById(R.id.post_edit_location);
        mDescription_Field = rootView.findViewById(R.id.post_edit_description);
        mUpload_Button = rootView.findViewById(R.id.post_edit_images);
        mSubmit_Btn = rootView.findViewById(R.id.post_edit_submit);
        uploaded_imageView = rootView.findViewById(R.id.post_edit_uploaded_imageView);

        Model.getInstance().getImage(mPost.getImage(), new Model.GetImageListener() {
            @Override
            public void onDone(Bitmap imageBitmap) {
                uploaded_imageView.setImageBitmap(imageBitmap);
                mPhotos = imageBitmap;
            }
        });

        mTitle_Field.setText(mPost.getTitle());
        mLocation_Field.setText(mPost.getLocation());
        mDescription_Field.setText(mPost.getDescription());
    }

    private void onSubmitButtonClicked() {
        if(!validateInput())
            return;


        final ProgressDialog dialog = ProgressDialog.show(getActivity(), "",
                "Please wait...", true);

        String title = mTitle_Field.getText().toString();
        String description = mDescription_Field.getText().toString();
        String location = mLocation_Field.getText().toString();
        Long currentDate = DateConverter.toTimestamp(new Date());

        mPost.setTitle(title);
        mPost.setDescription(description);
        mPost.setLocation(location);
        mPost.setUpdatedAt(currentDate);

        Model.getInstance().saveImage(mPhotos, new Model.SaveImageListener() {
            @Override
            public void onDone(String url) {
                if(url != null) {
                    mPost.setImage(url);
                    Model.getInstance().insertPost(mPost);
                }else{
                    Toast.makeText(getActivity(),"Editing post failed, please try again later.",Toast.LENGTH_LONG).show();
                }
                dialog.dismiss();
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }

    private void onUploadImageButtonClicked(){
        mUpload_Button.setError(null);
        startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Detects request codes
        if(requestCode==GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            try {
                mPhotos = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
                uploaded_imageView.setImageBitmap(mPhotos);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean validateInput(){
        boolean flag = true;
        if(TextUtils.isEmpty(mTitle_Field.getText().toString())){
            mTitle_Field.setError("Required");
            flag = false;
        }
        if(TextUtils.isEmpty(mDescription_Field.getText().toString())){
            mDescription_Field.setError("Required");
            flag = false;
        }
        if(TextUtils.isEmpty(mLocation_Field.getText().toString())){
            mLocation_Field.setError("Required");
            flag = false;
        }
        if(mPhotos == null) {
            mUpload_Button.setError("Required");
            flag = false;
        }
        else{
            Bitmap emptyBitmap = Bitmap.createBitmap(mPhotos.getWidth(), mPhotos.getHeight(), mPhotos.getConfig());
            if(mPhotos.sameAs(emptyBitmap)) {
                mUpload_Button.setError("Required");
                flag = false;
            }
        }
        return flag;
    }

}