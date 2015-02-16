package isiforum.isima.fr.isiforum.controllers;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import isiforum.isima.fr.isiforum.model.Post;
import isiforum.isima.fr.isiforum.model.PostSingleton;
import isiforum.isima.fr.isiforum.R;

/**
 * Defines an activity which shows and handles the view to display a message.
 */
public class DetailActivity extends ActionBarActivity {

    private static final String TAG = DetailActivity.class.getSimpleName();
    private int mIndex;
    private PostSingleton mSingleton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.mSingleton = PostSingleton.getInstance();
        this.mIndex = getIntent().getIntExtra("index", 0);
        bind();
        display();
    }

    /**
     * This method is in charge of binding all the activity's objects together. It defines the
     * handlers connected to the UI buttons for example.
     */
    private void bind(){
        Button btnPrevious = (Button) findViewById(R.id.btnPrevious);
        Button btnNext = (Button) findViewById(R.id.btnNext);
        Button btnAddMessage = (Button) findViewById(R.id.btnAdd);

        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DetailActivity.this.previousPost();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DetailActivity.this.nextPost();
            }
        });

        btnAddMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailActivity.this, CreatePostActivity.class);
                startActivity(intent);
            }
        });

    }

    /**
     * Displays the content of a message in the view.
     */
    private void display() {
        Post post = this.mSingleton.getPost(this.mIndex);

        TextView etAuthorName = (TextView) findViewById(R.id.tvAuthorNameValue);
        TextView etTitle = (TextView) findViewById(R.id.tvTitleValue);
        TextView etMessage = (TextView) findViewById(R.id.tvMessageValue);

        etAuthorName.setText(post.getAuthor());
        etTitle.setText(post.getTitle());
        etMessage.setText(post.getMessage());
    }

    /**
     * Increments the post's index to display and call the display method.
     */
    private void nextPost() {
        this.mIndex = (this.mIndex + 1) % this.mSingleton.getPostCount();
        this.display();
    }

    /**
     * Decrements the post's index to display and call the display method.
     */
    private void previousPost() {
        int count = this.mSingleton.getPostCount();
        this.mIndex = (this.mIndex + count - 1) % count;
        display();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id ==  android.R.id.home){
            finish();
        }
        else if(id == R.id.action_delete){

            // Opens a dialog box to make the user confirm the suppression
            new AlertDialog.Builder(this)
                    .setTitle("Delete post")
                    .setMessage("Are you sure you want to delete this post ?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DetailActivity.this.deleteCurrentPost();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A simple method to launch the suppression of the current post (the one which is displayed).
     */
    private void deleteCurrentPost(){
        Log.v(DetailActivity.TAG, "Deleting the post.");
        this.mSingleton.deletePost(this.mSingleton.getPost(this.mIndex));
        finish();
    }
}
