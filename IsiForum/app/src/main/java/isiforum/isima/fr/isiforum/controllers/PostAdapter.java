package isiforum.isima.fr.isiforum.controllers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import isiforum.isima.fr.isiforum.R;
import isiforum.isima.fr.isiforum.model.Post;

public class PostAdapter extends ArrayAdapter<Post> {


    public PostAdapter(Context context, List<Post> posts) {
        super(context, 0, posts);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Post post = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.post_item, parent, false);
        }

        TextView tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
        TextView tvAuthor = (TextView) convertView.findViewById(R.id.tvAuthor);

        tvTitle.setText(post.getTitle());
        tvAuthor.setText(post.getAuthor());

        return convertView;
    }
}
