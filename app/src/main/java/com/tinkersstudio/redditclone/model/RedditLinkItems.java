package com.tinkersstudio.redditclone.model;

/**
 * Object to present the item as the Reddit
 */
public class RedditLinkItems {

    private String mTitle;
    private String mUrl;


    public RedditLinkItems(String title){
        mTitle = title;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

}
