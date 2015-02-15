package isiforum.isima.fr.isiforum.model;

import com.google.gson.annotations.SerializedName;

/**
 * Defines a class to store the information of a post.
 */
public class Post {
    @SerializedName("id")
    private int mId;

    @SerializedName("title")
    private String mTitle;

    @SerializedName("author")
    private String mAuthor;

    @SerializedName("message")
    private String mMessage;

    public Post(String title, String author, String message){
        this.mTitle = title;
        this.mAuthor = author;
        this.mMessage = message;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getMessage() {
        return mMessage;
    }

    public int getId() {
        return mId;
    }

    @Override
    public String toString() {
        return "Post{" +
                "Title='" + mTitle + '\'' +
                ", Author='" + mAuthor + '\'' +
                ", Message='" + mMessage + '\'' +
                '}';
    }
}
