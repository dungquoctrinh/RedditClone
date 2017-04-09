package com.tinkersstudio.redditclone.util;

import android.util.Log;

import com.tinkersstudio.redditclone.model.RedditLinkItems;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Create the content for the Reddit thread
 */
public class LinkGenerator {
    private static final String TAG = "LINK_GENERATOR";
    private static String after;
    private static String currentSR;

    public static byte[] getURL(String subreddit) throws IOException {

        URL url;
        if (after == null || currentSR == null || !subreddit.equals(currentSR)) {
            //TODO: Change the content so that it get the /r correctly
            url = new URL("https://www.reddit.com/" + subreddit + ".json");
        } else {
            //TODO: Change the content so that it get the /r correctly
            url = new URL("https://www.reddit.com/" + subreddit + ".json?after="+after);
        }

        currentSR = subreddit;

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setReadTimeout(30000);
        if (urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new IOException(TAG + " no connection yo!");
        }
        try {
            InputStream in = urlConnection.getInputStream();
            byte[] URLBuffer = new byte[1024];
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int bytesRead = 0;
            while ((bytesRead = in.read(URLBuffer)) > 0) {
                out.write(URLBuffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            urlConnection.disconnect();
        }
    }

    private static String getJsonString(String subreddit) throws IOException {
        return new String(getURL(subreddit));
    }

    public static List<RedditLinkItems> parseJSON(String subreddit) {
        List<RedditLinkItems> links = new ArrayList<>();

        try {
            byte[] bytes = getURL(subreddit);
            JSONObject jBody = new JSONObject(getJsonString(subreddit));
            JSONObject jData = jBody.getJSONObject("data");
            JSONArray JChildren = jData.getJSONArray("children");

            after = jData.getString("after");
            Log.i(TAG, "after = "+after);

            for (int i = 0; i < JChildren.length(); i++) {
                JSONObject redditLinkDataObjects = JChildren.getJSONObject(i).getJSONObject("data");
                RedditLinkItems redditLinkItems = new RedditLinkItems(redditLinkDataObjects.getString("title"));
                redditLinkItems.setUrl("https://m.reddit.com"+redditLinkDataObjects.getString("permalink"));
                links.add(redditLinkItems);
            }
        } catch (IOException ioe) {
            Log.d(TAG, "parseJSON: " + ioe);
        } catch (JSONException j) {
            Log.d(TAG, "parseJSON: " + j);
        }

        Log.i(TAG, "parseJson Size is: " + links.size());
        return links;
    }
}


