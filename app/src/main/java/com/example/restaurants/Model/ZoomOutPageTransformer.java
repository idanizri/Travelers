package com.example.restaurants.Model;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.restaurants.MainScreenActivity;
import com.example.segev.restaurants.R;
import com.example.restaurants.RegisterActivity;
import com.google.firebase.auth.FirebaseUser;

public class ZoomOutPageTransformer implements ViewPager.PageTransformer {
    private static final float MIN_SCALE = 0.85f;
    private static final float MIN_ALPHA = 0.5f;

    public void transformPage(View view, float position) {
        int pageWidth = view.getWidth();
        int pageHeight = view.getHeight();

        if (position < -1) { // [-Infinity,-1)
            // This page is way off-screenz to the left.
            view.setAlpha(0);

        } else if (position <= 1) { // [-1,1]
            // Modify the default slide transition to shrink the page as well
            float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
            float vertMargin = pageHeight * (1 - scaleFactor) / 2;
            float horzMargin = pageWidth * (1 - scaleFactor) / 2;
            if (position < 0) {
                view.setTranslationX(horzMargin - vertMargin / 2);
            } else {
                view.setTranslationX(-horzMargin + vertMargin / 2);
            }

            // Scale the page down (between MIN_SCALE and 1)
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);

            // Fade the page relative to its size.
            view.setAlpha(MIN_ALPHA +
                    (scaleFactor - MIN_SCALE) /
                            (1 - MIN_SCALE) * (1 - MIN_ALPHA));

        } else { // (1,+Infinity]
            // This page is way off-screen to the right.
            view.setAlpha(0);
        }
    }

    public static class LoginActivity extends AppCompatActivity {
        private final static String LOG_TAG = LoginActivity.class.getSimpleName();

        //Views
        private EditText mEmailField;
        private EditText mPasswordField;
        private TextView createAccountText;
        //Views


        private Activity activity;
        //Buttons
        private Button mLoginButton;
        //Buttons


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);

            checkIfUserIsLoggedIn();

            initializeViews();
            activity = this;

            initializeButtons();
            bindButtons();
        }

        private void checkIfUserIsLoggedIn(){
            FirebaseUser currentUser = UserModel.getInstance().getCurrentUser();

            if(currentUser != null){
                Log.d(LOG_TAG,"User already logged in with " + currentUser.getEmail());
                Intent switchActivityIntent = new Intent(this,MainScreenActivity.class);
                switchActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(switchActivityIntent);
                finish();
            }
        }

        private void initializeViews(){
            mEmailField = findViewById(R.id.login_email_ed);
            mPasswordField = findViewById(R.id.login_password_ed);
        }

        private void initializeButtons(){
            mLoginButton = findViewById(R.id.login_button);
            createAccountText = findViewById(R.id.createAccount);
        }

        private void bindButtons(){
            createAccountText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent switchActivityIntent = new Intent(getApplicationContext(),RegisterActivity.class);
                    startActivity(switchActivityIntent);
                }
            });

            mLoginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final ProgressDialog dialog = ProgressDialog.show(activity, "",
                            "Logging in...", true);
                    hideKeyboard();

                    String email = mEmailField.getText().toString();
                    String password = mPasswordField.getText().toString();

                    UserModel currentUser = UserModel.getInstance();
                    currentUser.login(email, password, new UserModel.UserModelLoginListener() {
                        @Override
                        public void onLogin() {
                            Intent switchActivityIntent = new Intent(getApplicationContext(),MainScreenActivity.class);
                            switchActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            dialog.dismiss();
                            startActivity(switchActivityIntent);
                            finish();
                        }

                        @Override
                        public void onLoginFail() {
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(),"Login Failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }});
        }

        private void hideKeyboard(){
            View view = getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                if(imm != null)
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }
}