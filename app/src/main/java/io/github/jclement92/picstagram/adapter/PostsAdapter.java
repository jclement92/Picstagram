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
import androidx.recyclerview.widget.RecyclerView;

import com.parse.ParseFile;

import java.util.List;

import io.github.jclement92.picstagram.GlideApp;
import io.github.jclement92.picstagram.R;
import io.github.jclement92.picstagram.model.Post;

import static android.graphics.Typeface.BOLD;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    private static final String TAG = "PostsAdapter";

    private final Context context;
    private final List<Post> posts;

    public PostsAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Post> post) {
        posts.addAll(post);
        notifyDataSetChanged();
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
            tvUsername.setText(post.getUser().getUsername());

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

