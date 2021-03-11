package io.github.jclement92.picstagram.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.parse.ParseException;
import com.parse.ParseFile;

import io.github.jclement92.picstagram.GlideApp;
import io.github.jclement92.picstagram.R;
import io.github.jclement92.picstagram.model.Post;

import static android.graphics.Typeface.BOLD;
import static io.github.jclement92.picstagram.model.Post.DIFF_CALLBACK;

public class PostsAdapter extends PagedListAdapter<Post, PostsAdapter.ViewHolder> {

    private static final String TAG = "PostsAdapter";
    private final Context context;

    public PostsAdapter(Context context) {
        super(DIFF_CALLBACK);
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = getItem(position);
        if (post != null) {
            holder.bind(post);
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvUsername;
        private final ImageView ivImage;
        private final TextView tvDescription;
        private final TextView tvCreatedKey;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            ivImage = itemView.findViewById(R.id.ivImage);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvCreatedKey = itemView.findViewById(R.id.tvCreatedKey);
        }

        public void bind(Post post) {
            // Bind the post data into the view elements
            try {
                tvUsername.setText(post.getUser().fetchIfNeeded().getUsername());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            SpannableString spannableString = new SpannableString(post.getUser().getUsername());

            spannableString.setSpan(
                    new ForegroundColorSpan(Color.BLACK),
                    0,
                    spannableString.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            spannableString.setSpan(
                    new StyleSpan(BOLD),
                    0,
                    spannableString.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            tvDescription.setText(spannableString);
            tvDescription.append(" " + post.getDescription());

            tvCreatedKey.setText(post.getFormattedTimestamp(post.getCreatedAt().toString()));

            ParseFile image = post.getImage();

            if (image != null) {
                GlideApp
                        .with(context)
                        .load(post.getImage().getUrl())
                        .into(ivImage);
            }
        }
    }
}

