package isiforum.isima.fr.isiforum.model;


import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import static org.apache.http.protocol.HTTP.UTF_8;

/**
 * This singleton manages the model of the application. It is responsible to retrieve a list of posts
 * from a web service. Moreover it inherits from Observable in order to send notifications to a list
 * of observers when its internal state is changing (after the update of its list of posts for
 * example).
 */
public class PostSingleton extends Observable {

    private static final String TAG = PostSingleton.class.getSimpleName();
    private final static String WEB_SERVICE_URL = "http://forum.openium.fr/api/posts";

    private static PostSingleton mInstance = null;

    private Gson gson = new Gson();
    private List<Post> posts = new ArrayList<>();

    /**
     * The private constructor of the singleton. It calls the updatePosts method to retrieve the posts
     * from the web service.
     */
    private PostSingleton() {
        updatePosts();
    }

    /**
     * A thread safe method to get the instance of the singleton.
     * @return The freshly allocated singleton or the one which already exists.
     */
    public static PostSingleton getInstance() {
        if (PostSingleton.mInstance == null) {
            synchronized (PostSingleton.class) {
                if (PostSingleton.mInstance == null) {
                    PostSingleton.mInstance = new PostSingleton();
                }
            }
        }
        return PostSingleton.mInstance;
    }

    /**
     * The method launches an AsyncTask in order to retrieve a list of posts on the network. All the
     * retrieval logic is implemented in the AsyncTask.
     */
    public void updatePosts() {
        new RetrievePosts().execute();
    }

    /**
     * The method launches an AsyncTask to contact a web service and to add a new message to its
     * data source. All the sending logic is implemented in the AsyncTask.
     * @param post The message to add in data source.
     */
    public void sendPost(Post post) {
        new SendPosts().execute(post);
    }

    /**
     * The method launches an AsyncTask to delete a message of the data source (via the web service).
     * All the deletion logic is implemented in the AsyncTask.
     * @param post The message to delete.
     */
    public void deletePost(Post post){
        new DeletePosts().execute(post);
    }

    /**
     * A simple getter on the list of posts.
     * @return The singleton's list of posts.
     */
    public List<Post> getPosts() {
        return posts;
    }

    /**
     * A simple setter to assign a new list of posts to the singleton. When the affectation is done,
     * the singleton notifies its observers by sending a PostSingletonEvent with an associated code.
     * @param posts The new list of posts of the singleton.
     */
    private void setPosts(List<Post> posts) {
        // A fail-fast test
        assert posts != null;

        this.posts = posts;
        this.setChanged();
        this.notifyObservers(new PostSingletonEvent(PostSingletonEvent.EventCode.POSTS_LIST_UPDATED));
    }

    /**
     * The method adds the posts of the list passed as parameter to the singleton's list. After the
     * adding operation, it notifies the observers by sending a PostSingletonEvent with an associated
     * code.
     * @param posts The list of posts to add to the singleton.
     */
    public void addPosts(List<Post> posts) {
        for(Post post:posts){
            this.posts.add(post);
        }
    }

    /**
     * Returns the post contained in the singleton's list at the index passed as parameter.
     * @param index The index of the post in the singleton's list.
     * @return A post.
     */
    public Post getPost(int index) {
        return this.posts.get(index);
    }

    /**
     * Returns the number of posts contained in the singleton.
     * @return The number of posts in the singleton's list.
     */
    public int getPostCount() {
        return this.posts.size();
    }

    /**
     * Defines an AsyncTask to retrieve all the posts from the web service (with a network access).
     */
    class RetrievePosts extends AsyncTask<Void, Void, List<Post>> {

        @Override
        protected List<Post> doInBackground(Void... params) {
            Log.i(TAG, "Retrieving posts...");

            List<Post> posts;
            HttpClient client = new DefaultHttpClient();
            HttpGet getRequest = new HttpGet(PostSingleton.WEB_SERVICE_URL);

            try {
                HttpResponse response = client.execute(getRequest);

                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    InputStream inputStream = response.getEntity().getContent();
                    InputStreamReader reader = new InputStreamReader(inputStream);
                    posts = gson.fromJson(reader, new TypeToken<List<Post>>() {
                    }.getType());

                    Log.i(TAG, "Done retrieving posts: " + posts.size() + " posts found.");
                }
                else {
                    posts = null;
                    Log.e(PostSingleton.TAG, "Failed to load posts list. The web service returned" +
                            " the following message: \"" + response.getStatusLine().getReasonPhrase()
                            + "\".");
                }
            } catch (IOException e) {
                Log.e(PostSingleton.TAG, "Failed to retrieve posts due to a connection error.");
                e.printStackTrace();
                posts = null;
            }
            return posts;
        }

        @Override
        protected void onPostExecute(List<Post> posts) {
            // When the list of posts is not null, it means that the retrieval operation has
            // succeed.
            if (posts != null) {
                PostSingleton.getInstance().setPosts(posts);
            }
            // If an error occurs during the retrieval operation, the singleton sends a
            // PostSingletonEvent to notify its observers with an information message.
            else {
                PostSingleton.getInstance().setChanged();
                PostSingleton.getInstance().notifyObservers(new PostSingletonEvent("Could not " +
                        "retrieve the posts list... Please check your Internet connection and retry.",
                        PostSingletonEvent.EventCode.FAIL_TO_RETRIEVE_POSTS));
            }
        }
    }

    /**
     * Defines an AsyncTask to send a post to the web service (with a network access).
     */
    class SendPosts extends AsyncTask<Post, Integer, List<Post>> {

        @Override
        protected List<Post> doInBackground(Post... posts) {
            Log.i(PostSingleton.TAG, "Sending posts...");

            int count = posts.length;
            List<Post> sentPosts = new ArrayList<>();
            HttpClient client = new DefaultHttpClient();

            int i = 0;
            boolean errors = false;

            while (i < count && !errors){
                Post post = posts[i];
                publishProgress((int) ((i / (float) count) * 100));

                HttpPost postRequest = new HttpPost(PostSingleton.WEB_SERVICE_URL);

                List<NameValuePair> nameValuePairs = new ArrayList<>();
                nameValuePairs.add(new BasicNameValuePair("title", post.getTitle()));
                nameValuePairs.add(new BasicNameValuePair("author", post.getAuthor()));
                nameValuePairs.add(new BasicNameValuePair("message", post.getMessage()));
                nameValuePairs.add(new BasicNameValuePair("id", "0"));
                nameValuePairs.add(new BasicNameValuePair("timestamp", "0"));

                try {
                    postRequest.setEntity(new UrlEncodedFormEntity(nameValuePairs, UTF_8));
                    HttpResponse response = client.execute(postRequest);

                    if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                        InputStream inputStream = response.getEntity().getContent();
                        InputStreamReader reader = new InputStreamReader(inputStream);
                        Post sentPost = gson.fromJson(reader, Post.class);
                        sentPosts.add(sentPost);

                        Log.i(PostSingleton.TAG, "Succeed to send message \"" + sentPost.getTitle()
                                + "\" " + "(id: " + sentPost.getId() + ").");
                    } else {
                        sentPosts = null;
                        errors = true;
                        Log.e(PostSingleton.TAG, "Failed to send message \"" + post.getTitle() +
                                "\"." + " The web service returned the following message: \"" +
                                response.getStatusLine().getReasonPhrase() + "\".");
                    }
                } catch (IOException e) {
                    Log.e(PostSingleton.TAG, "Failed to send message \"" + post.getTitle() + "\"" +
                            " due to a connection error.");
                    sentPosts = null;
                    errors = true;
                    e.printStackTrace();
                }

                i += 1;
            }

            return sentPosts;
        }

        @Override
        protected void onPostExecute(List<Post> sentPosts) {
            // When the list of posts is not null, it means that the send operation has succeed.
            if (sentPosts != null) {
                PostSingleton.getInstance().addPosts(sentPosts);
            }

            // If an error occurs during the send operation, the singleton sends a
            // PostSingletonEvent to notify its observers with an information message.
            else {
                PostSingleton.getInstance().setChanged();
                PostSingleton.getInstance().notifyObservers(new PostSingletonEvent("An error occured" +
                        " during sending the posts.",
                        PostSingletonEvent.EventCode.FAIL_TO_SEND_POSTS));
            }
        }
    }

    /**
     * Defines an AsyncTask to delete a post on the data source (with a network access).
     */
    class DeletePosts extends AsyncTask<Post, Integer, Boolean>{

        @Override
        protected Boolean doInBackground(Post... postsToDelete) {
            Log.i(PostSingleton.TAG, "Deleting posts...");

            Boolean deletionSucceed = true;
            int count = postsToDelete.length;
            HttpClient client = new DefaultHttpClient();

            int i = 0;
            while (i < count && deletionSucceed){
                publishProgress((int) ((i / (float) count) * 100));

                Post post = postsToDelete[i];
                HttpDelete deleteRequest = new HttpDelete(PostSingleton.WEB_SERVICE_URL + "/" +
                post.getId());

                try {
                    HttpResponse response = client.execute(deleteRequest);

                    if(response.getStatusLine().getStatusCode() == HttpStatus.SC_NO_CONTENT){
                        Log.i(PostSingleton.TAG, "Succeed to delete message \"" + post.getTitle()
                        + "\".");
                    }
                    else {
                        deletionSucceed = false;
                        Log.e(PostSingleton.TAG, "Failled to delete message \"" + post.getTitle()
                                +"\". The web service returned the following message: \""+
                                response.getStatusLine().getReasonPhrase() + "\".");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(PostSingleton.TAG, "Failed to delete message \"" + post.getTitle() + "\"" +
                            " due to a connection error.");
                }

                i += 1;
            }

            return deletionSucceed;
        }

        @Override
        protected void onPostExecute(Boolean deletionSucceed){
            if(!deletionSucceed){
                PostSingleton.getInstance().notifyObservers(new PostSingletonEvent("An error" +
                        "occurred during the posts' deletion. Please check your Internet connection" +
                        " and retry.", PostSingletonEvent.EventCode.FAIL_TO_DELETE_POSTS));
            }
        }
    }
}
