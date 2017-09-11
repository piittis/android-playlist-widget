package com.wavy.spotifyplaylistwidget;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.wavy.spotifyplaylistwidget.listAdapters.PlaylistSelectionAdapter;
import com.wavy.spotifyplaylistwidget.network.SpotifyApi;
import com.wavy.spotifyplaylistwidget.utils.PicassoOnScrollListener;
import com.wavy.spotifyplaylistwidget.viewModels.PlaylistViewModel;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectActivity extends PlaylistWidgetConfigureActivityBase
        implements SpotifyApi.playlistsLoadedCallbackListener, SpotifyApi.spotifyApiErrorListener {

    private static final String TAG = "SelectActivity";

    // view elements
    @BindView(R.id.playlist_selection_list) RecyclerView mPlaylistsSelectionView;
    @BindView(R.id.selection_next_button) Button mNextButton;
    @BindView(R.id.selection_toolbar) Toolbar mToolbar;
    @BindView(R.id.swiperefresh) SwipeRefreshLayout mSwipeRefresh;

    private PlaylistSelectionAdapter mPlaylistSelectionAdapter;
    private String mToolbarTitle;
    private Boolean firstLoad = true;

    @Inject
    SpotifyApi mSpotifyApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        IoC.getComponent().inject(this);
        super.onCreate(savedInstanceState);

        mToolbarTitle = getString(R.string.select_playlists);

        setContentView(R.layout.activity_select);

        ButterKnife.bind(this);

        mToolbar.setTitle(mToolbarTitle);
        setSupportActionBar(mToolbar);

        mNextButton.setOnClickListener((v) -> startArrangeActivity());

        mSwipeRefresh.setOnRefreshListener(this::manualUpdate);

        initializePlaylistSelectionList();

        mSpotifyApi.setErrorListener(this);

        mPlaylists.clearAll();
        if (mSpotifyApi.isAccessTokenValid()) {
            loadPlaylists();
        } else {
            // Do authentication first, playlists will be loaded in onActivityResult instead.
            doAuthentication();
        }
    }

    private void initializePlaylistSelectionList() {
        mPlaylistSelectionAdapter = new PlaylistSelectionAdapter(mPlaylists.getPlaylists(), this);
        mPlaylistsSelectionView.setAdapter(mPlaylistSelectionAdapter);
        mPlaylistsSelectionView.setLayoutManager(new LinearLayoutManager(this));
        mPlaylistsSelectionView.addOnScrollListener(new PicassoOnScrollListener(this));
        mPlaylistSelectionAdapter.setOnClickListener((v) -> updateSelectedPlaylists());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
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
                manualUpdate();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    private void loadPlaylists() {
        if (firstLoad || mPlaylists.getPlaylistsCount() == 0) {
            firstLoad = false;
            this.findViewById(R.id.playlists_loading_indicator).setVisibility(View.VISIBLE);
            mPlaylistsSelectionView.scheduleLayoutAnimation();
        }
        mSpotifyApi.getPlaylists(0, this);
    }

    @Override
    public void onPlaylistsLoaded(int offset, ArrayList<PlaylistViewModel> newPlaylists) {

        this.findViewById(R.id.playlists_loading_indicator).setVisibility(View.GONE);

        mSwipeRefresh.setRefreshing(false);

        if (offset == 0) {
            // If this is first batch of playlists, update whole dataset.
            mPlaylists.initializePlaylists(newPlaylists);
            mPlaylistSelectionAdapter.notifyDataSetChanged();
        } else {
            mPlaylists.addPlaylists(newPlaylists);
            // Notify only about the added items to avoid stuttering..
            int len = mPlaylists.getPlaylistsCount();
            for(int i = offset; i < len; i++) {
                mPlaylistSelectionAdapter.notifyItemChanged(i);
            }
        }
    }

    private void startArrangeActivity() {
        Intent intent = new Intent(getApplicationContext(), ArrangeActivity.class);
        startNextConfigurationActivity(intent);
    }

    private void manualUpdate() {
        logEvent("manual_data_refresh");
        loadPlaylists();
    }

    private void selectAll(boolean selected) {
        logEvent((selected) ? "select_all" : "remove_selections");

        if (mPlaylists != null && mPlaylistSelectionAdapter != null) {
            for (PlaylistViewModel pl : mPlaylists.getPlaylists()) {
                pl.selected = selected;
            }
            mPlaylistSelectionAdapter.notifyDataSetChanged();
            updateSelectedPlaylists();
        }
    }

    private void updateSelectedPlaylists() {

        mPlaylists.updateSelectedPlaylists();
        if (mPlaylists.getSelectedPlaylistsCount() > 0) {
            mToolbar.setTitle(String.format(getString(R.string.playlists_selected_count),
                    mPlaylists.getSelectedPlaylistsCount(), mPlaylists.getPlaylistsCount()));

            mNextButton.setEnabled(true);
        } else {
            mToolbar.setTitle(mToolbarTitle);
            mNextButton.setEnabled(false);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.d(TAG, "onActivityResult " + requestCode);
        super.onActivityResult(requestCode, resultCode, intent);

        // Authentication done.
        if (requestCode == AUTH_REQUEST && resultCode == RESULT_OK) {
            loadPlaylists();
        }
    }

    @Override
    public void onSpotifyApiError(String reason) {
        logEvent("api_error");
        Toast.makeText(getApplicationContext(), getString(R.string.spotify_api_error) + " (" + reason + ")"
                , Toast.LENGTH_LONG).show();
    }
}