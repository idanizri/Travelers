package com.example.restaurants;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


import com.example.restaurants.Fragments.RestaurantsFragment;
import com.example.restaurants.Fragments.HomeFragment;
import com.example.restaurants.Fragments.SavedFragment;
import com.example.restaurants.Fragments.UserProfileFragment;
import com.example.restaurants.Model.UserModel;
import com.example.restaurants.Model.ZoomOutPageTransformer;
import com.example.segev.restaurants.R;

//TODO CHANGE ALL THE WAY OF INITALIZING FRAGMENTS TO NEWINSTANCE

//Is there a way to go back to a specific fragment?
public class MainScreenActivity extends AppCompatActivity {
    private final static String LOG_TAG = MainScreenActivity.class.getSimpleName();


    //Action Toolbar and it's Drawer
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Toolbar toolbar;
    //Action Toolbar and it's Drawer

    //Views
    private TextView header_tv;
    private NavigationView nav_view;
    //Views


    final int REQUEST_WRITE_STORAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        mDrawerLayout = findViewById(R.id.drawerLayout);

        mDrawerLayout.addDrawerListener(mToggle);

        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.open, R.string.close);
        mToggle.syncState();

        nav_view = findViewById(R.id.nav_view);

        setupDrawerContent(nav_view);

        initializeHeaderEmail(nav_view);

        nav_view.getMenu().getItem(0).setChecked(true);



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_STORAGE);
            }
        }

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            Fragment fragment = null;
            try {
                fragment = HomeFragment.class.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            transaction.replace(R.id.flContent, fragment, "Home");
            transaction.commit();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_WRITE_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {}
                return;
            }
        }
    }



    @Override
    public void onBackPressed() {
        if(mDrawerLayout.isDrawerOpen(GravityCompat.START)){
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }else if(getSupportFragmentManager().getBackStackEntryCount() == 0) {
            moveTaskToBack(true);
        }else{
            super.onBackPressed();
        }
    }

    private void setupDrawerContent(NavigationView navigationView){
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    private void selectDrawerItem(MenuItem menuItem) {
        menuItem.setChecked(true);
        mDrawerLayout.closeDrawer(GravityCompat.START);


        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        Class fragmentClass = null;
        int itemThatWasSelected = menuItem.getItemId();

        switch(itemThatWasSelected) {
            case R.id.nav_home:
                fragmentClass = HomeFragment.class;
                break;

            case R.id.nav_experiences:
                fragmentClass = RestaurantsFragment.class;
                break;



            case R.id.nav_user_profile:
                fragmentClass = UserProfileFragment.class;
                break;


            case R.id.nav_saved:
                fragmentClass = SavedFragment.class;
                break;


            case R.id.nav_logout:
                menuItem.setChecked(false);
                logoutUser();
                break;
        }

        if(fragmentClass != null){
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
                // Insert the fragment by replacing any existing fragment
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().addToBackStack(null).replace(R.id.flContent, fragment).commit();
        }
    }


    private void initializeHeaderEmail(NavigationView navigationView){
        View header = navigationView.getHeaderView(0);
        header_tv = header.findViewById(R.id.nav_header_tv);
        header_tv.setText(UserModel.getInstance().getCurrentUser().getEmail());
    }

    private void logoutUser(){
        AlertDialog.Builder builder;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
             else
                builder = new AlertDialog.Builder(this);

            builder.setTitle("Sign out")
                    .setMessage("Are you sure you want to sign out?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d(LOG_TAG,"Logging out from user : " + UserModel.getInstance().getCurrentUser().getEmail());
                            UserModel.getInstance().signOutAccount();
                            Intent switchActivityIntent = new Intent(getApplicationContext(),ZoomOutPageTransformer.LoginActivity.class);
                            switchActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(switchActivityIntent);
                            finish();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
    }
}
