package com.wavy.spotifyplaylistwidget;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Button;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.wavy.spotifyplaylistwidget.listAdapters.PlaylistSelectionAdapter;
import com.wavy.spotifyplaylistwidget.network.SpotifyApi;
import com.wavy.spotifyplaylistwidget.utils.PicassoOnScrollListener;
import com.wavy.spotifyplaylistwidget.viewModels.PlaylistViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectActivity extends AppCompatActivity
        implements SpotifyApi.playlistsLoadedCallbackListener {

    private static final String TAG = "SelectActivity";
    private ArrayList<PlaylistViewModel> mPlaylists = new ArrayList<>();
    private HashSet<String> mSelectedPlaylistIds = new HashSet<>();
    private SpotifyApi mSpotifyApi = new SpotifyApi();
    private Boolean isFirstLoad = true;

    // view elements
    @BindView(R.id.playlist_selection_list) RecyclerView mPlaylistsSelectionView;
    @BindView(R.id.selection_next_button) Button mNextButton;
    @BindView(R.id.selection_toolbar) Toolbar mToolbar;
    @BindView(R.id.swiperefresh) SwipeRefreshLayout mSwipeRefresh;

    private PlaylistSelectionAdapter mPlaylistSelectionAdapter;
    final String mToolbarTitle = "Select playlists";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_select);
        ButterKnife.bind(this);

        mToolbar.setTitle(mToolbarTitle);
        setSupportActionBar(mToolbar);

        mNextButton.setOnClickListener((v) -> startArrangeActivity());

        //todo refresh throws exception if scrolling at the same time
        mSwipeRefresh.setOnRefreshListener(this::loadPlaylists);

        // First try to get initial selections from saved state.
        String[] initialSelections = null;
        if (savedInstanceState != null) {
            initialSelections = savedInstanceState.getStringArray("selectedPlaylistIds");
        }
        // No saved state, try to get initial selections from intent.
        if (initialSelections == null) {
            initialSelections = getIntent().getStringArrayExtra("selectedPlaylistIds");
        }
        if (initialSelections != null) {
            Collections.addAll(mSelectedPlaylistIds, initialSelections);
        }

        initializePlaylistSelectionList();

        if (SpotifyApi.isAccessTokenSet()) {
            loadPlaylists();
        } else {
            // Go get access token first.
            startAuthActivity();
        }
    }

    private void initializePlaylistSelectionList() {
        mPlaylistSelectionAdapter = new PlaylistSelectionAdapter(mPlaylists, getApplicationContext());
        mPlaylistsSelectionView.setAdapter(mPlaylistSelectionAdapter);
        mPlaylistsSelectionView.setLayoutManager(new LinearLayoutManager(this));
        mPlaylistsSelectionView.addOnScrollListener(new PicassoOnScrollListener(getApplicationContext()));
        mPlaylistSelectionAdapter.setOnClickListener((v) -> updateSelectedPlaylists());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArray("selectedPlaylistIds",
                mSelectedPlaylistIds.toArray(new String[mSelectedPlaylistIds.size()]));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.selectionmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.select_all:
                selectAll(true);
                return true;
            case R.id.remove_selections:
                selectAll(false);
                return true;
            case R.id.refresh:
                mSwipeRefresh.setRefreshing(true);
                loadPlaylists();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private void loadPlaylists() {
        mPlaylists.clear();
        // todo react to possible errors
        mSpotifyApi.getPlaylists(0, this);
    }

    @Override
    public void onPlaylistsLoaded(int offset, ArrayList<PlaylistViewModel> newPlaylists) {

        Log.d(TAG, "onPlaylistsLoaded, offset " + offset);
        // Restore selected status
        for (PlaylistViewModel pl : newPlaylists) {
            pl.selected = mSelectedPlaylistIds.contains(pl.id);
        }

        mPlaylists.addAll(newPlaylists);

        if (isFirstLoad) {
            isFirstLoad = false;
            Log.d(TAG, "animate in");
            mPlaylistsSelectionView.scheduleLayoutAnimation();
        }

        mSwipeRefresh.setRefreshing(false);

        if (offset == 0) {
            // If this is first batch of playlists, update whole dataset.
            mPlaylistSelectionAdapter.notifyDataSetChanged();
        } else {
            // Otherwise notify only about the added items to avoid stuttering.
            int len = mPlaylists.size();
            for(int i = offset; i < len; i++) {
                mPlaylistSelectionAdapter.notifyItemChanged(i);
            }
        }
    }

    private void startArrangeActivity() {
        Intent intent = new Intent(getApplicationContext(), ArrangeActivity.class);

        ArrayList<PlaylistViewModel> selected = new ArrayList<>(mSelectedPlaylistIds.size());
        for (PlaylistViewModel pl : mPlaylists)
            if (pl.selected)
                selected.add(pl);

        intent.putParcelableArrayListExtra("mPlaylists", selected);
        startActivity(intent);
    }

    private void startAuthActivity() {
        // Authenticate and return back to this activity.
        Intent intent = new Intent(getApplicationContext(), AuthActivity.class);
        intent.putExtra("returnToActivity", true);
        startActivityForResult(intent, 99);
    }

    private void selectAll(boolean selected) {
        if (mPlaylists != null && mPlaylistSelectionAdapter != null) {
            for (PlaylistViewModel pl : mPlaylists) {
                pl.selected = selected;
            }
            mPlaylistSelectionAdapter.notifyDataSetChanged();
            updateSelectedPlaylists();
        }
    }

    private void updateSelectedPlaylists() {
        mSelectedPlaylistIds.clear();

        for (PlaylistViewModel pl : mPlaylists)
            if (pl.selected)
                mSelectedPlaylistIds.add(pl.id);

        if (mSelectedPlaylistIds.size() > 0) {
            mToolbar.setTitle(String.format("%d / %d valittu", mSelectedPlaylistIds.size(), mPlaylists.size()));
            mNextButton.setEnabled(true);
        } else {
            mToolbar.setTitle(mToolbarTitle);
            mNextButton.setEnabled(false);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Authentication done.
        if (requestCode == 99) {
            loadPlaylists();
        }
    }
}