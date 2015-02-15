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

public class DetailActivity extends ActionBarActivity {

    private static final String TAG = DetailActivity.class.getSimpleName();
    private int mId;
    private PostSingleton mSingleton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        this.mSingleton = PostSingleton.getInstance();
        this.mId = getIntent().getIntExtra("index", 0);
        bind();
        display();
    }

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

    private void display() {
        Post post = this.mSingleton.getPost(this.mId);

        TextView etAuthorName = (TextView) findViewById(R.id.tvAuthorNameValue);
        TextView etTitle = (TextView) findViewById(R.id.tvTitleValue);
        TextView etMessage = (TextView) findViewById(R.id.tvMessageValue);

        etAuthorName.setText(post.getAuthor());
        etTitle.setText(post.getTitle());
        etMessage.setText(post.getMessage());
    }

    private void nextPost() {
        this.mId = (this.mId + 1) % this.mSingleton.getPostCount();
        this.display();
    }

    private void previousPost() {
        int count = this.mSingleton.getPostCount();
        this.mId = (this.mId + count - 1) % count;
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
