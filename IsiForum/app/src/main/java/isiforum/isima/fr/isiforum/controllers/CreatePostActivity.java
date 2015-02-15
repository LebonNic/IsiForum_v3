package isiforum.isima.fr.isiforum.controllers;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import isiforum.isima.fr.isiforum.model.Post;
import isiforum.isima.fr.isiforum.model.PostSingleton;
import isiforum.isima.fr.isiforum.R;

/**
 * Defines an activity which is in charge of displaying and handling a view to send a post.
 */
public class CreatePostActivity extends ActionBarActivity {

    private static final String TAG = CreatePostActivity.class.getSimpleName();

    EditText    etTitle,
            etAuthor,
            etMessage;

    PostSingleton mSingleton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.etTitle = (EditText) findViewById(R.id.etTitleValue);
        this.etAuthor = (EditText) findViewById(R.id.etAuthorNameValue);
        this.etMessage = (EditText) findViewById(R.id.etMessageValue);
        this.mSingleton = PostSingleton.getInstance();
        bind();
    }

    /**
     * This method is in charge of binding all the activity's objects together. It defines the
     * handlers connected to the UI buttons for example.
     */
    private void bind(){
        Button btnSend = (Button)findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateUIFields()){
                    Post postToSend = createPostToSend();
                    CreatePostActivity.this.mSingleton.sendPost(postToSend);
                    finish();
                }
                else {
                    Toast.makeText(CreatePostActivity.this, "You need to complete all the fields " +
                            "before to send a message.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_post, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Creates an initialized Post from the UI fields.
     * @return An initialized Post.
     */
    private Post createPostToSend(){
        String  title,
                author,
                message;

        Post post;

        title = this.etTitle.getText().toString();
        author = this.etAuthor.getText().toString();
        message = this.etMessage.getText().toString();
        post = new Post(title, author, message);

        return post;
    }

    /**
     * Controls that all the UI text fields are filled with something.
     * @return True if all the fields are filled, false if they are not.
     */
    private boolean validateUIFields(){
        boolean valid = false;

        if(!isEmpty(this.etTitle) && !isEmpty(this.etAuthor) && !isEmpty(this.etMessage)){
            valid = true;
        }

        return valid;
    }

    /**
     * Checks that the EditText passed as parameter contains some text.
     * @param editText The EditText passed as parameter.
     * @return True if the EditText is empty, false if it is not.
     */
    private Boolean isEmpty(EditText editText){
        boolean isEmpty = false;
        if(editText.getText().toString().trim().length() == 0){
            isEmpty = true;
        }

        return isEmpty;
    }
}
