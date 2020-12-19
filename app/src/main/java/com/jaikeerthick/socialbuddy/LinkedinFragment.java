package com.jaikeerthick.socialbuddy;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.AbdAllahAbdElFattah13.linkedinsdk.ui.LinkedInUser;
import com.AbdAllahAbdElFattah13.linkedinsdk.ui.linkedin_builder.LinkedInFromFragmentBuilder;
import com.jaikeerthick.socialbuddy.databinding.FragmentLinkedinBinding;
import com.squareup.picasso.Picasso;

import static android.app.Activity.RESULT_OK;

public class LinkedinFragment extends Fragment {

    FragmentLinkedinBinding binding;
    private static final int LINKEDIN_REQUEST_CODE = 1;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @SuppressLint("CommitPrefEdits")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding =  FragmentLinkedinBinding.inflate(inflater, container, false);

        binding.tick.setVisibility(View.GONE);
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        editor = preferences.edit();

        binding.linkedinLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LinkedInFromFragmentBuilder.Companion.getInstance(LinkedinFragment.this)
                        .setClientID("YOUR_CLIENT_ID")
                        .setClientSecret("YOUR_SECRET_ID")
                        .setRedirectURI("YOUR_REDIRECT_URL")
                        .authenticate(LINKEDIN_REQUEST_CODE);
            }
        });

        binding.linkedinLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preferences.edit().clear().apply();
                binding.userNameLinkedIn.setText("User Name");
                binding.emailLinkedIn.setText("useremail@gmail.com");

                binding.tick.setVisibility(View.GONE);
                Picasso.with(getActivity())
                        .load(R.drawable.student)
                        .into(binding.profileImageLinkedIn);
                binding.linkedinLoginButton.setVisibility(View.VISIBLE);
                binding.linkedinLogoutButton.setVisibility(View.GONE);
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        if (preferences.contains("name"))
        {
            binding.userNameLinkedIn.setText(preferences.getString("name", ""));
            binding.emailLinkedIn.setText(preferences.getString("email", ""));

            binding.tick.setVisibility(View.VISIBLE);
            Picasso.with(getActivity())
                    .load(preferences.getString("imageurl", ""))
                    .placeholder(R.drawable.spinnerjaikeerthick)
                    .into(binding.profileImageLinkedIn);
            binding.linkedinLoginButton.setVisibility(View.GONE);
            binding.linkedinLogoutButton.setVisibility(View.VISIBLE);

        }else {
            binding.userNameLinkedIn.setText("User Name");
            binding.emailLinkedIn.setText("useremail@gmail.com");

            binding.tick.setVisibility(View.GONE);
            Picasso.with(getActivity())
                    .load(R.drawable.student)
                    .into(binding.profileImageLinkedIn);
            binding.linkedinLoginButton.setVisibility(View.VISIBLE);
            binding.linkedinLogoutButton.setVisibility(View.GONE);
        }
        super.onStart();
    }

    private void setUserData(LinkedInUser user) {


        String name = user.getFirstName()+" "+user.getLastName();
       binding.userNameLinkedIn.setText(name);
       binding.emailLinkedIn.setText(user.getEmail());


        if (user.getProfilePictureUrl() != null && !user.getProfilePictureUrl().isEmpty()) {

            Picasso.with(getActivity())
                    .load(user.getProfilePictureUrl())
                    .placeholder(R.drawable.spinnerjaikeerthick)
                    .into(binding.profileImageLinkedIn);

        }

        binding.tick.setVisibility(View.VISIBLE);

        editor.putString("name", name);
        editor.putString("email", user.getEmail());
        editor.putString("imageurl", user.getProfilePictureUrl());
        editor.apply();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LINKEDIN_REQUEST_CODE && data != null) {
            if (resultCode == RESULT_OK) {

                //Successfully signed in and retrieved data
                LinkedInUser user = data.getParcelableExtra("social_login");
                setUserData(user);

            } else {
                //print the error


                if (data.getIntExtra("err_code", 0) == LinkedInFromFragmentBuilder.ERROR_USER_DENIED) {
                    //user denied access to account
                    Toast.makeText(getContext(), "User Denied Access", Toast.LENGTH_SHORT).show();
                } else if (data.getIntExtra("err_code", 0) == LinkedInFromFragmentBuilder.ERROR_USER_DENIED) {
                    //some error occured
                    Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}