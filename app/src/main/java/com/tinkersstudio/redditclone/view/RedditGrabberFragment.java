package com.tinkersstudio.redditclone.view;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.tinkersstudio.redditclone.R;
import com.tinkersstudio.redditclone.model.RedditLinkItems;
import com.tinkersstudio.redditclone.util.LinkGenerator;
import com.tinkersstudio.redditclone.util.WebViewActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment of Reddit
 */
public class RedditGrabberFragment extends Fragment {
    private static final String TAG = "REDDIT_GRABBER_FRAGMENT";
    public static final String LINK_URL = "LINK_URL";

    private RecyclerView mRecyclerview;
    private RedditGrabberAdapter mAdapter;
    protected List<RedditLinkItems> mList;

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    //next+prev group
    private LinearLayout mNavLinearLayout;

    //private Button mNextButton;
    //private Button mPrevButton;

    private static String subreddit;

    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 5;
    int firstVisibleItem, visibleItemCount, totalItemCount;


    /**
     * Static factory method to initialize and setup a new fragment
     * @return fragment An initialize RedditGrabberFragment
     */
    public static RedditGrabberFragment newInstance() {
        RedditGrabberFragment fragment = new RedditGrabberFragment();
        return fragment;
    }

    /**
     * Constructor
     */
    public RedditGrabberFragment()
    {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mList = new ArrayList<RedditLinkItems>();
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_reddit_recycler_view, container, false);
        new getRedditInfo().execute();

        //mNavLinearLayout = (LinearLayout) v.findViewById(R.id.navigation_button);
        //mNextButton = (Button) v.findViewById(R.id.next_button);
        //mPrevButton = (Button) v.findViewById(R.id.prev_button);

        mDrawerLayout = (DrawerLayout) v.findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(),
                mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                new getRedditInfo().execute();
                updateUI();

            }
        };

        mDrawerLayout.addDrawerListener(mDrawerToggle);
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        //item list
        mDrawerList = (ListView) v.findViewById(R.id.left_drawer);

        final String[] mDrawerItems = getResources().getStringArray(R.array.subreddit);
        mDrawerList.setAdapter(new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, mDrawerItems));
        mDrawerList.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                subreddit = mDrawerItems[position];
                Log.i(TAG, "onItemClick: " + subreddit);
                mDrawerLayout.closeDrawer(Gravity.LEFT);
                new getRedditInfo().execute();
                updateUI();
            }
        });

        mRecyclerview = (RecyclerView) v.findViewById(R.id.my_recycler_view);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerview.setLayoutManager(mLayoutManager);

        mRecyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                visibleItemCount = mRecyclerview.getChildCount();
                totalItemCount = mLayoutManager.getItemCount();
                firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();

                if (totalItemCount <= (firstVisibleItem + visibleItemCount)) {
                    // End has been reached

                    Log.i(TAG, "end called");

                    mRecyclerview.setPadding(0, 0, 0, 150);
                    //mNavLinearLayout.setVisibility(View.VISIBLE);
                } else {
                    //mNavLinearLayout.setVisibility(View.INVISIBLE);
                    mRecyclerview.setPadding(0, 0, 0, 0);
                }
            }
        });

        updateUI();
        mDrawerToggle.syncState();

        return v;
    }


    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_reddit_recycler_view, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.menu_item_refresh:
                new getRedditInfo().execute();
                updateUI();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void updateUI() {
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (subreddit == null) {
            subreddit = "/r/all";
        }
        actionBar.setTitle(subreddit);

        if (mAdapter == null) {
            mAdapter = new RedditGrabberAdapter(mList);
            mRecyclerview.setAdapter(mAdapter);
        } else {
            mAdapter.updateList(mList);
            mAdapter.notifyDataSetChanged();
        }
    }

    /**Get the information from reddit*/
    public class getRedditInfo extends AsyncTask<Void, Void, List<RedditLinkItems>> {
        @Override
        protected List<RedditLinkItems> doInBackground(Void... params) {
            if (subreddit == null) {
                return LinkGenerator.parseJSON("/r/all");
            }
            return LinkGenerator.parseJSON(subreddit);
        }

        @Override
        protected void onPostExecute(List<RedditLinkItems> redditLinkItemses) {
            Log.i(TAG, "onPostExecute: redditLinkItemses size is " + redditLinkItemses.size());
            mList = redditLinkItemses;
            Log.i(TAG, "onPostExecute: mList size is " + mList.size());
            updateUI();
        }
    }
}



