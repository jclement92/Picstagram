package io.github.jclement92.picstagram.fragments;

import android.os.Bundle;
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
import androidx.lifecycle.LiveData;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import io.github.jclement92.picstagram.ParseDataSourceFactory;
import io.github.jclement92.picstagram.ParsePositionalDataSource;
import io.github.jclement92.picstagram.R;
import io.github.jclement92.picstagram.adapter.PostsAdapter;
import io.github.jclement92.picstagram.model.Post;

/**
 * A simple {@link Fragment} subclass.
 */
public class PostsFragment extends Fragment {

    private static final String TAG = "PostsFragment";

    Toolbar toolbar;
    RecyclerView rvPosts;
    PostsAdapter adapter;
    SwipeRefreshLayout swipeRefreshLayout;

    // This data should be encapsulated in a ViewModel, but used here for simplicity
    LiveData<PagedList<Post>> posts;
    ParseDataSourceFactory dataSourceFactory;

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

        // Get views
        toolbar = view.findViewById(R.id.toolbar);
        rvPosts = view.findViewById(R.id.rvPosts);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        // Setup Toolbar
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);

        // Setup RecyclerView
        adapter = new PostsAdapter(getContext());
        rvPosts.setAdapter(adapter);
        rvPosts.setLayoutManager(new LinearLayoutManager(getContext()));

        // Setup PagedList configuration
        PagedList.Config pagedListConfig =
                new PagedList.Config.Builder()
                        .setEnablePlaceholders(true)
                        .setPrefetchDistance(10)
                        .setInitialLoadSizeHint(20)
                        .setPageSize(10)
                        .build();

        // Posts data source factory
        dataSourceFactory = new ParseDataSourceFactory();

        // Get new posts
        swipeRefreshLayout.setOnRefreshListener(() -> {

            // Get the data source
            ParsePositionalDataSource dataSource =
                    dataSourceFactory.getMutableLiveData().getValue();

            // Invalidate the data source if not null
            if (dataSource != null) {
                dataSource.invalidate();
            }
        });

        // Build list of posts based on PageList configuration
        posts = new LivePagedListBuilder<>(dataSourceFactory, pagedListConfig).build();
        posts.observe(this, posts -> {
            adapter.submitList(posts);
            swipeRefreshLayout.setRefreshing(false);
//            Log.i(TAG, "Total posts: " + posts.size());
//            Log.i(TAG, "Initial loaded posts: " + posts.getLoadedCount());
        });

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
    }
}