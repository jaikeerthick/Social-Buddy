package com.jaikeerthick.socialbuddy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.jaikeerthick.socialbuddy.databinding.FragmentFacebookBinding;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;


public class FacebookFragment extends Fragment {

    FragmentFacebookBinding binding;
    private CallbackManager callbackManager;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        checkLoginStatus();
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(getActivity());
        callbackManager = CallbackManager.Factory.create();

        binding = FragmentFacebookBinding.inflate(inflater, container, false);

        binding.tick.setVisibility(View.GONE);
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        editor = preferences.edit();


        binding.loginButton.setReadPermissions(Arrays.asList("email", "public_profile"));
        binding.loginButton.setFragment(this);

        binding.loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });


        return binding.getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);

        super.onActivityResult(requestCode, resultCode, data);
    }


    AccessTokenTracker tokenTracker = new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken)
        {

            if (currentAccessToken==null){

                preferences.edit().clear().apply();
                binding.userName.setText("User Name");
                binding.email.setText("useremail@gmail.com");

                binding.tick.setVisibility(View.GONE);
                Picasso.with(getContext())
                        .load(R.drawable.student)
                        .into(binding.profileImage);
            }else
                loadUserProfile(currentAccessToken);


        }
    };

   private void checkLoginStatus(){
       if (AccessToken.getCurrentAccessToken()!=null){
           loadUserProfile(AccessToken.getCurrentAccessToken());
       }
   }

    private void loadUserProfile(AccessToken newAccessToken){

        GraphRequest request = GraphRequest.newMeRequest(newAccessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {

                try {
                    String firstName = object.getString("first_name");
                    String lastName = object.getString("last_name");
                    String email = object.getString("email");
                    String id = object.getString("id");



                    String imageUrl = "https://graph.facebook.com/" + id + "/picture?type=normal";


                    setProfile(firstName, lastName, email,  imageUrl);


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "first_name,last_name,email,id,picture.type(normal)");
        request.setParameters(parameters);
        request.executeAsync();
    }

    @Override
    public void onStart() {
        if (preferences.contains("name_fb"))
        {
            binding.userName.setText(preferences.getString("name_fb", ""));
            binding.email.setText(preferences.getString("email_fb", ""));
            binding.tick.setVisibility(View.VISIBLE);


        }else {
            binding.userName.setText("User Name");
            binding.email.setText("useremail@gmail.com");
            binding.tick.setVisibility(View.GONE);

        }
        super.onStart();
    }

    private void setProfile(String firstName, String lastName, String email, String imageUrl) {

        binding.userName.setText(firstName+" "+lastName);
        binding.email.setText(email);


        Picasso.with(getContext())
                .load(imageUrl)
                .placeholder(R.drawable.spinnerjaikeerthick)
                .into(binding.profileImage);
        binding.tick.setVisibility(View.VISIBLE);

        editor.putString("name_fb", firstName+" "+lastName);
        editor.putString("email_fb", email);
        editor.apply();

    }
}