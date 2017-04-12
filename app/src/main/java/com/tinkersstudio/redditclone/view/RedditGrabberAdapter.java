package com.tinkersstudio.redditclone.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tinkersstudio.redditclone.R;
import com.tinkersstudio.redditclone.model.RedditLinkItems;

import java.util.List;

/**
 * Created by Owner on 4/12/2017.
 */

public class RedditGrabberAdapter extends RecyclerView.Adapter<RedditGrabberViewHolder> {

    private List<RedditLinkItems> mNumbers;

    public RedditGrabberAdapter(List<RedditLinkItems> numbers) {
        mNumbers = numbers;
    }

    @Override
    public RedditGrabberViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.list_reddit_items, parent, false);
        return new RedditGrabberViewHolder(view);
    }

    public void updateList(List<RedditLinkItems> list) {
        mNumbers = list;
    }

    @Override
    public void onBindViewHolder(RedditGrabberViewHolder holder, int position) {
        holder.bind(mNumbers.get(position).getTitle(), mNumbers.get(position).getUrl());
    }

    @Override
    public int getItemCount() {
        return mNumbers.size();
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    public Object getItem(int position) {
        return this.mNumbers.get(position);
    }


    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return position;
    }
}