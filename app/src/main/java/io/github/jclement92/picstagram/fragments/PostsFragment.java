package io.github.jclement92.picstagram.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import io.github.jclement92.picstagram.Post;
import io.github.jclement92.picstagram.PostsAdapter;
import io.github.jclement92.picstagram.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PostsFragment extends Fragment {

    private static final String TAG = "PostsFragment";
    private RecyclerView rvPosts;
    private PostsAdapter adapter;
    private List<Post> allPosts;

    private SwipeRefreshLayout swipeRefreshLayout;

    public PostsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Set icons and inflate the layout for this fragment
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_posts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);

        rvPosts = view.findViewById(R.id.rvPosts);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            adapter.clear();
            queryPosts();
            swipeRefreshLayout.setRefreshing(false);
        });

        allPosts = new ArrayList<>();
        adapter = new PostsAdapter(getContext(), allPosts);

        // Set the adapter on the recyclerview
        rvPosts.setAdapter(adapter);
        // Set the LayoutManager on the recyclerview
        rvPosts.setLayoutManager(new LinearLayoutManager(getContext()));

        queryPosts();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
    }

    protected void queryPosts() {
        // Specify which class to query
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.setLimit(20);
        query.addDescendingOrder(Post.KEY_CREATED_KEY);

        query.findInBackground((posts, e) -> {
            if (e != null) {
                Log.e(TAG, "Issue with getting posts", e);
                return;
            }
            for(Post post: posts) {
                Log.i(TAG, "Post: " + post.getDescription() + ", Username: " + post.getUser().getUsername());
            }
            adapter.addAll(posts);
        });
    }
}