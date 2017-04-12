package com.tinkersstudio.redditclone.view;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.tinkersstudio.redditclone.R;
import com.tinkersstudio.redditclone.model.RedditLinkItems;
import com.tinkersstudio.redditclone.util.WebViewActivity;

import static com.tinkersstudio.redditclone.view.RedditGrabberFragment.LINK_URL;

/**
 * Created by Owner on 4/12/2017.
 */

public class RedditGrabberViewHolder extends RecyclerView.ViewHolder {
    private Button mListButton;
    private String mReddit;


    public RedditGrabberViewHolder(final View itemView) {
        super(itemView);
        mListButton = (Button) itemView.findViewById(R.id.title_button_view);


        //listen when it is clicked
        mListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = itemView.getContext();
                Intent intent = new Intent(context, WebViewActivity.class);
                intent.putExtra(LINK_URL, mReddit);
                context.startActivity(intent);
            }
        });
    }

    /**
     * Set the content of the button
     * @param title
     */
    public void bind(String title, String url) {
        mListButton.setText(title);
        mReddit = url;
    }

}