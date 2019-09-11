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
import com.example.restaurants.Model.UserModel;
import com.example.segev.restaurants.R;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;


public class PostNewFragment extends Fragment {
    private static final String LOG_TAG = PostNewFragment.class.getSimpleName();

    final int GET_FROM_GALLERY = 3;

    private EditText mTitle_Field;
    private EditText mDescription_Field;
    private EditText mLocation_Field;
    private Button mSubmit_Btn;

    private ImageView uploaded_imageView;


    private Bitmap mPhotos;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_new_post, container, false);
        initializeViews(rootView);

        uploaded_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onUploadImageButtonClicked();
            }
        });
        mSubmit_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSubmitButtonClicked();
            }
        });
        return rootView;
    }

    private void initializeViews(View rootView) {
        uploaded_imageView = rootView.findViewById(R.id.post_new_uploaded_imageView);
        mDescription_Field = rootView.findViewById(R.id.post_new_description);
        mLocation_Field = rootView.findViewById(R.id.post_new_location);
        mTitle_Field = rootView.findViewById(R.id.post_new_title);
        mSubmit_Btn = rootView.findViewById(R.id.post_new_submit);
    }

    private void onSubmitButtonClicked() {
        if(!validateInput())
            return;

        final ProgressDialog dialog = ProgressDialog.show(getActivity(), "",
                "Please wait...", true);


        final String userIdWhoPosted = UserModel.getInstance().getCurrentUser().getUid();
        final String title = mTitle_Field.getText().toString();
        final String location = mLocation_Field.getText().toString();
        final String description = mDescription_Field.getText().toString();
        final Long currentDate = DateConverter.toTimestamp(new Date());
        final String currentUserEmail = UserModel.getInstance().getCurrentUserEmail();
        Model.getInstance().saveImage(mPhotos, new Model.SaveImageListener() {
            @Override
            public void onDone(String url) {

                if(url != null) {
                    Post postToInsert = new Post(userIdWhoPosted, currentDate, title, location, description,url, currentUserEmail);
                    Model.getInstance().insertPost(postToInsert);
                }else{
                    Toast.makeText(getActivity(),"Adding post failed, please try again later.",Toast.LENGTH_LONG).show();
                }
                dialog.dismiss();
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }

    private void onUploadImageButtonClicked(){
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
            Toast.makeText(getActivity(),"Upload a photo", Toast.LENGTH_LONG).show();
            flag = false;
        }
        return flag;
    }
}

