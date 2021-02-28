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

import com.parse.ParseQuery;
import com.parse.ParseUser;

import io.github.jclement92.picstagram.LoginActivity;
import io.github.jclement92.picstagram.Post;
import io.github.jclement92.picstagram.R;

public class ProfileFragment extends PostsFragment {
    private static final String TAG = "ProfileFragment";

    private Button btnLogout;

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
            if(e != null) {
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

    @Override
    protected void queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.setLimit(20);
        query.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());
        query.addDescendingOrder(Post.KEY_CREATED_KEY);

        query.findInBackground((posts, e) -> {
            if (e != null) {
                Log.e(TAG, "Issue with getting posts", e);
                return;
            }
            for(Post post: posts) {
                Log.i(TAG, "Post: " + post.getDescription() + ", Username: " + post.getUser().getUsername());
            }
            allPosts.addAll(posts);
            adapter.notifyDataSetChanged();
        });
    }
}
