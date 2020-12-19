package com.jaikeerthick.socialbuddy;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.fxn.OnBubbleClickListener;
import com.jaikeerthick.socialbuddy.databinding.ActivityMainBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    int current_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));

        getSupportFragmentManager().beginTransaction().replace(R.id.container,
                new HomeFragment()).commit();
        current_id = R.id.intro_menu;

        binding.bubbleTabBar.addBubbleListener(new OnBubbleClickListener() {
            @Override
            public void onBubbleClick(int i) {

                if (current_id!=i) {

                    Fragment selectedFragment = null;

                    switch (i) {
                        case R.id.linkedin_menu:
                            selectedFragment = new LinkedinFragment();
                            current_id = i;
                            break;
                        case R.id.facebook_menu:
                            selectedFragment = new FacebookFragment();
                            current_id = i;
                            break;
                        case R.id.intro_menu:
                            selectedFragment = new HomeFragment();
                            current_id = i;
                            break;
                    }

                    assert selectedFragment != null;
                    getSupportFragmentManager().beginTransaction().replace(R.id.container,
                            selectedFragment).commit();
                }

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        for (Fragment fragment : getSupportFragmentManager().getFragments()) {

            fragment.onActivityResult(requestCode, resultCode, data);
        }

        super.onActivityResult(requestCode, resultCode, data);

    }
}