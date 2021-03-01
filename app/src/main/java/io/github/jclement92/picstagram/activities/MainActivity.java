package io.github.jclement92.picstagram.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import io.github.jclement92.picstagram.R;
import io.github.jclement92.picstagram.fragments.ComposeFragment;
import io.github.jclement92.picstagram.fragments.PostsFragment;
import io.github.jclement92.picstagram.fragments.ProfileFragment;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private final FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                menuItem -> {
                    Fragment fragment = null;
                    int itemId = menuItem.getItemId();
                    if (itemId == R.id.action_home) {
                        fragment = new PostsFragment();
                    } else if (itemId == R.id.action_compose) {
                        fragment = new ComposeFragment();
                    } else if (itemId == R.id.action_profile) {
                        fragment = new ProfileFragment();
                    }

                    if (fragment != null) {
                        fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                    }

                    return true;
                });
        // Set default selection
        bottomNavigationView.setSelectedItemId(R.id.action_home);
    }
}