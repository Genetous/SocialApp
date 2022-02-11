package com.mubo.socialapp.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mubo.socialapp.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.mubo.socialapp.login_signup.signup_fragment;
import com.mubo.socialapp.main.ui.post.PostFragment;
import com.mubo.socialapp.main.ui.settings.PostsFragment;
import com.mubo.socialapp.main.ui.settings.SettingsFragment;
import com.opensooq.supernova.gligar.GligarPicker;
public class Home extends AppCompatActivity {
    NavController navController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setItemIconTintList(null);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_search,R.id.navigation_post, R.id.navigation_notifications,
                R.id.navigation_settings)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
        if(this.getIntent().getBooleanExtra("notification",false)){
            navView.setSelectedItemId(R.id.navigation_notifications);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode){
            case 123 : {
                String pathsList[]= data.getExtras().getStringArray(GligarPicker.IMAGES_RESULT); // return list of selected images paths.

                PostFragment p=getCurrentVisibleFragment();
                p.setImagePath(pathsList[0]);
                break;
            }
            case 124 : {
                String pathsList[]= data.getExtras().getStringArray(GligarPicker.IMAGES_RESULT); // return list of selected images paths.

                SettingsFragment p=getSettingsFragment();
                p.setImagePath(pathsList[0]);
                break;
            }
        }
    }
    private PostFragment getCurrentVisibleFragment() {
        NavHostFragment navHostFragment = (NavHostFragment)getSupportFragmentManager().getPrimaryNavigationFragment();
        FragmentManager fragmentManager = navHostFragment.getChildFragmentManager();
        Fragment loginFragment = fragmentManager.getPrimaryNavigationFragment();
        if(loginFragment instanceof PostFragment){
            return (PostFragment)loginFragment;
        }
        return null;
    }
    private SettingsFragment getSettingsFragment() {
        NavHostFragment navHostFragment = (NavHostFragment)getSupportFragmentManager().getPrimaryNavigationFragment();
        FragmentManager fragmentManager = navHostFragment.getChildFragmentManager();
        Fragment loginFragment = fragmentManager.getPrimaryNavigationFragment();
        if(loginFragment instanceof SettingsFragment){
            return (SettingsFragment) loginFragment;
        }
        return null;
    }
}