package isiforum.isima.fr.isiforum.controllers;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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

        return super.onOptionsItemSelected(item);
    }
}
