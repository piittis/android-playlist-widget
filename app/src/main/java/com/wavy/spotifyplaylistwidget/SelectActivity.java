package com.wavy.spotifyplaylistwidget;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Button;

import com.wavy.spotifyplaylistwidget.listAdapters.PlaylistSelectionAdapter;
import com.wavy.spotifyplaylistwidget.models.Playlist;
import com.wavy.spotifyplaylistwidget.network.SpotifyApi;

import java.util.ArrayList;
import java.util.HashSet;

public class SelectActivity extends AppCompatActivity
        implements SpotifyApi.playlistsLoadedCallbackListener {

    private ArrayList<Playlist> mPlaylists;
    private HashSet<String> mSelectedPlaylistIds = new HashSet<>();
    private SpotifyApi mSpotifyApi = new SpotifyApi();

    // view elements
    private RecyclerView mPlaylistsSelectionView;
    private PlaylistSelectionAdapter mPlaylistSelectionAdapter;
    private Button mNextButton;
    private Toolbar mToolbar;
    private SwipeRefreshLayout mSwipeRefresh;

    final String mToolbarTitle = "Select playlists";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //todo log in with spotify, get access token, etc.

        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_select);

        mToolbar = (Toolbar) findViewById(R.id.selection_toolbar);
        mToolbar.setTitle(mToolbarTitle);
        setSupportActionBar(mToolbar);

        mNextButton = (Button) findViewById(R.id.selection_next_button);
        mNextButton.setOnClickListener((v) -> goToArrangeScreen());

        mSwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        mSwipeRefresh.setOnRefreshListener(this::loadPlaylists);

        if (savedInstanceState != null) {
            this.mPlaylists = savedInstanceState.getParcelableArrayList("mPlaylists");
        }

        if (this.mPlaylists == null) {
            this.mPlaylists = new ArrayList<>();
        }

        initializePlaylistSelectionList();

        if (this.mPlaylists.size() == 0) {
            loadPlaylists();
        }
        updateSelectedPlaylists();
    }

    private void initializePlaylistSelectionList() {
        mPlaylistSelectionAdapter = new PlaylistSelectionAdapter(mPlaylists, getApplicationContext());
        mPlaylistsSelectionView = (RecyclerView) this.findViewById(R.id.playlist_selection_list);
        mPlaylistsSelectionView.setAdapter(mPlaylistSelectionAdapter);
        mPlaylistsSelectionView.setLayoutManager(new LinearLayoutManager(this));
        mPlaylistSelectionAdapter.setOnClickListener((v) -> updateSelectedPlaylists());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("mPlaylists", mPlaylists);
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
        mSpotifyApi.getPlaylists(this);
    }

    @Override
    public void onPlaylistsLoaded(ArrayList<Playlist> newPlaylists) {
        mPlaylists.clear();
        mPlaylists.addAll(newPlaylists);

        //restore selected status
        for (Playlist pl : mPlaylists) {
            pl.selected = mSelectedPlaylistIds.contains(pl.id);
        }

        mPlaylistSelectionAdapter.notifyDataSetChanged();

        mSwipeRefresh.setRefreshing(false);
    }

    private void goToArrangeScreen() {

        Intent intent = new Intent(getApplicationContext(), ArrangeActivity.class);

        ArrayList<Playlist> selected = new ArrayList<>();
        for (Playlist pl : mPlaylists)
            if (pl.selected)
                selected.add(pl);

        intent.putParcelableArrayListExtra("mPlaylists", selected);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in_hard, R.anim.fade_out_hard);
    }

    private void selectAll(boolean selected) {
        if (this.mPlaylists != null && this.mPlaylistSelectionAdapter != null) {
            for (Playlist pl : this.mPlaylists) {
                pl.selected = selected;
            }
            mPlaylistSelectionAdapter.notifyDataSetChanged();
            updateSelectedPlaylists();
        }
    }

    private void updateSelectedPlaylists() {

        mSelectedPlaylistIds.clear();

        for (Playlist pl : this.mPlaylists)
            if (pl.selected)
                mSelectedPlaylistIds.add(pl.id);

        if (mSelectedPlaylistIds.size() > 0) {
            mToolbar.setTitle(String.format("%d / %d valittu", mSelectedPlaylistIds.size(), this.mPlaylists.size()));
            mNextButton.setEnabled(true);
        } else {
            mToolbar.setTitle(this.mToolbarTitle);
            mNextButton.setEnabled(false);
        }
    }
}