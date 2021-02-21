package io.github.jclement92.picstagram;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;

import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;

    private EditText etCaption;
    private ImageView ivPostImage;
    private Button btnSubmit;
    private Button btnLogout;
    private ProgressBar pbLoading;

    private File photoFile;
    private String photoFileName = "photo.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        etCaption = findViewById(R.id.etCaption);
        ivPostImage = findViewById(R.id.ivPostImage);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnLogout = findViewById(R.id.btnLogout);
        pbLoading = findViewById(R.id.pbLoading);

//        queryPosts();
        btnSubmit.setOnClickListener(v -> {
            String description = etCaption.getText().toString();

            if (description.isEmpty()) {
                Toast.makeText(MainActivity.this, "Description cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            if (photoFile == null || ivPostImage.getDrawable() == null) {
                Toast.makeText(MainActivity.this, "There is no image!", Toast.LENGTH_SHORT).show();
                return;
            }
            ParseUser currentUser = ParseUser.getCurrentUser();
            pbLoading.setVisibility(View.VISIBLE);
            savePost(description, currentUser, photoFile);

            etCaption.setText("");       // Clear text
            ivPostImage.setImageResource(0); // Clear image
        });

        btnLogout.setOnClickListener(v -> logout());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        int itemId = item.getItemId();
        if (itemId == R.id.miAddPhoto) {
            launchCamera();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void logout() {
        ParseUser.logOutInBackground(e -> {
            if (e != null) {
                Log.e(TAG, "Error logging out");
            } else {
                Log.i(TAG, "Logged out successfully");
                goLoginActivity();
            }
        });
    }

    private void goLoginActivity() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish(); // Prevent user from returning to home screen when logged out
    }

    private void launchCamera() {
        // create implicit intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // File reference for future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(MainActivity.this, "io.github.jclement92.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(this.getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    // Uri: Uniform Resource Identifier - A string that "Unambiguously identifies the resource"
    // Uri in this case is the image that we captured.
    private File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(this.getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = rotateBitmapOrientation(photoFile.getAbsolutePath());
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                ivPostImage.setImageBitmap(takenImage);

            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // https://guides.codepath.org/android/Accessing-the-Camera-and-Stored-Media#rotating-the-picture
    private Bitmap rotateBitmapOrientation(String photoFilePath) {
        // Create and configure BitmapFactory
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoFilePath, bounds);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        Bitmap bm = BitmapFactory.decodeFile(photoFilePath, opts);
        // Read EXIF Data
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(photoFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String orientString = Objects.requireNonNull(exif).getAttribute(ExifInterface.TAG_ORIENTATION);
        int orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;
        int rotationAngle = 0;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;
        // Rotate Bitmap
        Matrix matrix = new Matrix();
        matrix.setRotate(rotationAngle, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
        // Return result
        return Bitmap.createBitmap(bm, 0, 0, bounds.outWidth, bounds.outHeight, matrix, true);
    }

    private void queryPosts() {
        // Specify which class to query
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);

        query.findInBackground((posts, e) -> {
            if (e != null) {
                Log.e(TAG, "Issue with getting posts", e);
                return;
            }
            for(Post post: posts) {
                Log.i(TAG, "Post: " + post.getDescription() + ", Username: " + post.getUser().getUsername());
            }
        });
    }

    private void savePost(String description, ParseUser currentUser, File photoFile) {
        Post post = new Post();
        post.setDescription(description);
        post.setImage(new ParseFile(photoFile));
        post.setUser(currentUser);

        // Create a Handler instance on the main thread
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            post.saveInBackground(e -> {
                if (e != null) {
                    Log.e(TAG, "Error while saving", e);
                    Toast.makeText(MainActivity.this, "Error while saving!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.i(TAG, "Post save was successful!!");
            });
            pbLoading.setVisibility(View.INVISIBLE);
        }, 250);

    }
}