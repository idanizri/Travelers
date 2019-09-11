package com.example.restaurants;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.restaurants.Model.UserModel;
import com.example.segev.restaurants.R;

public class RegisterActivity extends AppCompatActivity {
    private static final String LOG_TAG = RegisterActivity.class.getSimpleName();

    //Views
    private EditText mEmailField;
    private EditText mPasswordField;
    //Views

    //Buttons
    private Button mRegisterButton;
    //Buttons

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);

        initializeViews();
        initializeButtons();
    }

    private void initializeViews(){
        mEmailField = findViewById(R.id.register_email_ed);
        mPasswordField = findViewById(R.id.register_password_ed);
    }

    private void initializeButtons(){
        mRegisterButton = findViewById(R.id.register_button);
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRegisterButtonClicked();
            }
        });
    }


    private void onRegisterButtonClicked(){
        final ProgressDialog dialog = ProgressDialog.show(this, "",
                "Creating A New User...", false);


        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();

        UserModel userModel = UserModel.getInstance();

        userModel.createUser(email, password, new UserModel.UserModelRegisterListener() {
            @Override
            public void onRegister() {
                Intent switchActivityIntent = new Intent(getApplicationContext(),MainScreenActivity.class);
                switchActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                dialog.dismiss();
                startActivity(switchActivityIntent);
                finish();
            }

            @Override
            public void onRegisterFail() {
                dialog.dismiss();
                Toast.makeText(getApplicationContext(),"Register Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}