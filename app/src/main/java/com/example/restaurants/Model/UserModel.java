package com.example.restaurants.Model;

import android.support.annotation.NonNull;
import android.util.Log;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserModel {

    private static final String LOG_TAG = UserModel.class.getSimpleName();

    private FirebaseAuth mAuth;
    private static final Object LOCK = new Object();
    private static UserModel instance;

    public static UserModel getInstance(){
        if(instance == null){
            synchronized (LOCK){
                instance = new UserModel();
            }
        }
        return instance;
    }

    private UserModel() {
        mAuth = FirebaseAuth.getInstance();
    }

    public void login(String email, String password, final UserModelLoginListener listener){
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                    listener.onLogin();
                else
                    listener.onLoginFail();
                }
        });
    }

    public void createUser(final String email, final String password, final UserModelRegisterListener listener) {
        Log.d(LOG_TAG,"Create user has been called with " + email + " " + password);
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())
                    listener.onRegister();
                else
                    listener.onRegisterFail();
            }
        });
    }

    public FirebaseUser getCurrentUser(){ return mAuth.getCurrentUser(); }
    public void signOutAccount(){ mAuth.signOut(); }

    public String getCurrentUserEmail(){
        return UserModel.getInstance().getCurrentUser().getEmail();
    }


    public interface UserModelLoginListener {
        void onLogin();
        void onLoginFail();
    }
    public interface UserModelRegisterListener{
        void onRegister();
        void onRegisterFail();
    }
}
