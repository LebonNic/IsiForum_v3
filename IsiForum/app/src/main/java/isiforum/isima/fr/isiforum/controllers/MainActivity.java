package isiforum.isima.fr.isiforum.controllers;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.lang.Override;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import isiforum.isima.fr.isiforum.model.Post;
import isiforum.isima.fr.isiforum.model.PostSingleton;
import isiforum.isima.fr.isiforum.R;
import isiforum.isima.fr.isiforum.model.PostSingletonEvent;

/**
 * Defines the main activity of the application. His role is to display a list of posts which have
 * been previously downloaded on the Internet (via a web service). The class implements the
 * Observer interface in order to receive the PostSingleton notifications (see Observer pattern).
 */
public class MainActivity extends ActionBarActivity implements Observer {

    private static final String TAG = MainActivity.class.getSimpleName();

    private PostSingleton mSingleton;
    private PostAdapter mAdapter;
    private ListView mListView;
    private boolean isComingFromCreatePostActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.v(MainActivity.TAG, "onCreate called. Going to \"Created\" state.");
        this.mSingleton = PostSingleton.getInstance();
        this.mAdapter = new PostAdapter(this, new ArrayList<Post>());
        this.mListView = (ListView) findViewById(R.id.lvPostsList);
        this.isComingFromCreatePostActivity = false;
        bind();
    }

    /**
     * This method is in charge of binding all the activity's objects together. It defines the
     * handlers connected to the UI buttons for example or add the activity to the observers list of
     * the PostSingleton.
     */
    private void bind() {
        this.mSingleton.addObserver(this);
        this.mListView.setAdapter(this.mAdapter);

        this.mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.v(MainActivity.TAG, "Going to DetailActivity.");
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra("index", position);
                startActivity(intent);
            }
        });

        Button btnSend = (Button) findViewById(R.id.btnAddMessage);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(MainActivity.TAG, "Going to CreatePostActivity.");
                MainActivity.this.isComingFromCreatePostActivity = true;
                Intent intent = new Intent(MainActivity.this, CreatePostActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh){
            PostSingleton.getInstance().updatePosts();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(MainActivity.TAG, "onPause called. Going to \"Paused\" state.");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v(MainActivity.TAG, "onStop called. Going to \"Stopped\" state.");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.v(MainActivity.TAG, "onRestart called. Going to \"Started\" state.");
    }

    /**
     * Refreshes the MainActivity's list view.
     */
    @Override
    protected void onResume() {
        super.onResume();
        Log.v(MainActivity.TAG, "onResume called. Going to \"Resumed\" state.");

        // This test is useful to not execute the update operation two times.
        // When a new Post is created and sent to the web service, the PostSingleton notifies its
        // observers by sending an event. This event is processed by the MainActivity in order to
        // update the list view with the new post. So when the application goes back to the
        // MainActivity from the CreatePostActivity, no need to call the update method a second time.
        if(!this.isComingFromCreatePostActivity)
            this.mSingleton.updatePosts();
        else
            this.isComingFromCreatePostActivity = false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v(MainActivity.TAG, "onStart called. Going to \"Started\" state.");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(MainActivity.TAG, "onDestroy called. Going to \"Destroyed\" state.");
    }

    /**
     * This method processes the notifications sent by the PostSingleton.
     * @param observable The object which sent the notification.
     * @param data The data associated to the notification.
     */
    @Override
    public void update(Observable observable, Object data) {

        // Checks whether the data are of PostSingletonEvent type
        if (data instanceof PostSingletonEvent) {
            Log.v(MainActivity.TAG, "Processes the update notification.");
            PostSingletonEvent event = (PostSingletonEvent) data;

            switch (event.getCode()) {
                case POSTS_LIST_UPDATED:
                    Log.v(MainActivity.TAG, "Updates the list view.");
                    this.mAdapter.clear();
                    this.mAdapter.addAll(this.mSingleton.getPosts());
                    this.mAdapter.notifyDataSetChanged();
                    break;

                case FAIL_TO_RETRIEVE_POSTS:
                    Log.v(MainActivity.TAG, "A problem occurred during the posts' fetching.");
                    Toast.makeText(MainActivity.this, event.getMessage(), Toast.LENGTH_LONG).show();
                    break;

                case FAIL_TO_SEND_POST:
                    Log.v(MainActivity.TAG, "A problem occurred during the posts' sending.");
                    Toast.makeText(MainActivity.this, event.getMessage(), Toast.LENGTH_SHORT).show();
                    break;

                default:
                    Log.v(MainActivity.TAG, "Unprocessed event.");
                    break;
            }
        } else {
            Log.v(MainActivity.TAG, "Ignores the notification.");
        }
    }
}
