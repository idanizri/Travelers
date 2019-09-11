package com.example.restaurants.Fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.restaurants.Model.UserModel;
import com.example.segev.restaurants.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserProfileFragment extends Fragment {
    private static final String LOG_TAG = UserProfileFragment.class.getSimpleName();
    private TextView mUsername;
    private EditText mCurrentPassword;
    private EditText mNewPassword;
    private Button mSubmitButton;

    private Button mToggleCurrentPasswordShow;
    private Button mToggleNewPasswordShow;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_user_profile, container, false);
        initializeViews(rootView);
        mUsername.setText(UserModel.getInstance().getCurrentUser().getEmail()+ "");

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onChangePasswordClicked();
            }
        });

        initializeShowToggleButton(mToggleCurrentPasswordShow,mCurrentPassword);
        initializeShowToggleButton(mToggleNewPasswordShow,mNewPassword);

        return rootView;
    }

    private void initializeShowToggleButton(Button mTogglePasswordShow, final EditText mPassword) {
        mTogglePasswordShow.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {

                switch ( event.getAction() ) {
                    case MotionEvent.ACTION_DOWN:
                        mPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                        break;
                    case MotionEvent.ACTION_UP:
                        mPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        break;
                }
                return true;
            }
        });
    }

    private void initializeViews(View rootView) {
        mUsername = rootView.findViewById(R.id.user_profile_name);
        mCurrentPassword = rootView.findViewById(R.id.user_profile_current_password);
        mNewPassword = rootView.findViewById(R.id.user_profile_new_password);
        mSubmitButton = rootView.findViewById(R.id.user_profile_submit);
        mToggleCurrentPasswordShow = rootView.findViewById(R.id.user_profile_current_password_show);
        mToggleNewPasswordShow = rootView.findViewById(R.id.user_profile_new_password_show);
    }

    private void onChangePasswordClicked(){

        final ProgressDialog dialog = ProgressDialog.show(getActivity(), "",
                "Please wait...", true);

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


// Get auth credentials from the user for re-authentication. The example below shows
// email and password credentials but there are multiple possible providers,
// such as GoogleAuthProvider or FacebookAuthProvider.
        AuthCredential credential = EmailAuthProvider
                .getCredential(user.getEmail(), mCurrentPassword.getText().toString());

// Prompt the user to re-provide their sign-in credentials
        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            user.updatePassword(mNewPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    dialog.dismiss();
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getActivity(),"Password updated",Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(getActivity(),"Failed to change password",Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        } else {
                            dialog.dismiss();
                            Toast.makeText(getActivity(),"Failed to change password",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        NavigationView view = getActivity().findViewById(R.id.nav_view);
        view.getMenu().getItem(3).setChecked(true);
        getActivity().setTitle("User Profile");
    }

}
