package io.github.jclement92.picstagram.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.parse.ParseUser;

import io.github.jclement92.picstagram.R;
import io.github.jclement92.picstagram.activities.LoginActivity;

public class ProfileFragment extends PostsFragment {

    private static final String TAG = "ProfileFragment";

    Button btnLogout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);

        ParseUser currentUser = ParseUser.getCurrentUser();
        toolbar.setTitle(currentUser.getUsername());

        btnLogout = view.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> logout());
    }

    private void logout() {
        ParseUser.logOutInBackground(e -> {
            if (e != null) {
                Log.e(TAG,"Error logging out");
            } else {
                Log.i(TAG,"Logged out successfully");
                goLoginActivity();
            }
        });
    }

    private void goLoginActivity() {
        Intent intent = new Intent(getContext(), LoginActivity.class);
        startActivity(intent);
//        finish(); // Prevent user from returning to home screen when logged out
    }
}
