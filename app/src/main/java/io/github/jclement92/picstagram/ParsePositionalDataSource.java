package io.github.jclement92.picstagram;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.paging.PositionalDataSource;

import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.List;

import io.github.jclement92.picstagram.model.Post;

public class ParsePositionalDataSource extends PositionalDataSource<Post> {
    private static final String TAG = "ParsePositionalDataSrc";

    // define basic query here
    public ParseQuery<Post> getQuery() {
        return ParseQuery.getQuery(Post.class).orderByDescending("createdAt");
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback<Post> callback) {
        Log.i(TAG, "loadInitial: Loading initial set of posts...");

        // get basic query
        ParseQuery<Post> query = getQuery();

        // Use values passed when PagedList was created.
        query.setLimit(params.requestedLoadSize);
        query.setSkip(params.requestedStartPosition);

        try {
            // loadInitial() should run queries synchronously so the initial list will not be empty
            // subsequent fetches can be async
            int count = query.count();
            List<Post> posts = query.find();

            Log.i(TAG, "loadInitial: Requested load size: " + params.requestedLoadSize);
            Log.i(TAG, "loadInitial: Posts count: " + query.find().size());

            // return info back to PagedList
            callback.onResult(posts, params.requestedStartPosition, count);
        } catch (ParseException e) {
            // retry logic here
            Log.e(TAG, "loadInitial: " + ParseException.OBJECT_NOT_FOUND, e);
        }
    }

    @Override
    public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback<Post> callback) {
        Log.i(TAG, "loadRange: Loading next set of posts...");

        // get basic query
        ParseQuery<Post> query = getQuery();

        query.setLimit(params.loadSize);

        // fetch the next set from a different offset
        query.setSkip(params.startPosition);

        try {
            // run queries synchronously since function is called on a background thread
            List<Post> posts = query.find();
            Log.i(TAG, "loadRange: Posts count: " + posts.size());

            // return info back to PagedList
            callback.onResult(posts);
        } catch (ParseException e) {
            // retry logic here
            Log.e(TAG, "loadRange: " + ParseException.OBJECT_NOT_FOUND, e);
        }
    }
}
