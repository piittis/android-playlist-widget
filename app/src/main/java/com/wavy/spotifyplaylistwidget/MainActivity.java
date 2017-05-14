package com.wavy.spotifyplaylistwidget;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity
        implements SpotifyApi.playlistsLoadedCallbackListener {

    private ArrayList<Playlist> playlists;
    private HashSet<String> selectedPlaylistIds = new HashSet<>();
    private SpotifyApi spotifyApi = new SpotifyApi();

    // view elements
    private RecyclerView playlistsSelectionView;
    private PlaylistSelectionAdapter playlistSelectionAdapter;
    private Button nextButton;
    private Toolbar toolbar;
    private SwipeRefreshLayout swipeRefresh;

    final String toolbarDefaultTitle = "Select playlists";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //todo log in with spotify, get access token, etc.

        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.selection_toolbar);
        toolbar.setTitle(toolbarDefaultTitle);
        setSupportActionBar(toolbar);

        nextButton = (Button) findViewById(R.id.selection_next_button);
        nextButton.setOnClickListener((v) -> goToArrangeScreen());

        swipeRefresh = (SwipeRefreshLayout)findViewById(R.id.swiperefresh);
        swipeRefresh.setOnRefreshListener(this::loadPlaylists);

        if (savedInstanceState != null) {
            this.playlists = savedInstanceState.getParcelableArrayList("playlists");
        }

        if (this.playlists == null) {
            this.playlists = new ArrayList<>();
        }

        initializePlaylistSelectionList();

        if (this.playlists.size() == 0) {
            loadPlaylists();
        }
    }

    private void initializePlaylistSelectionList() {
        playlistSelectionAdapter = new PlaylistSelectionAdapter(playlists);
        playlistsSelectionView = (RecyclerView)this.findViewById(R.id.playlist_selection_list);
        playlistsSelectionView.setAdapter(playlistSelectionAdapter);
        playlistsSelectionView.setLayoutManager(new LinearLayoutManager(this));
        playlistSelectionAdapter.setOnClickListener((v) -> updateSelectedPlaylists());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("playlists", playlists);
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
                swipeRefresh.setRefreshing(true);
                loadPlaylists();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private void loadPlaylists() {
        spotifyApi.getPlaylists(this);
    }

    @Override
    public void onPlaylistsLoaded(ArrayList<Playlist> newPlaylists) {
        playlists.clear();
        playlists.addAll(newPlaylists);

        //restore selected status
        for (Playlist pl : playlists) {
            pl.selected = selectedPlaylistIds.contains(pl.id);
        }

        playlistSelectionAdapter.notifyDataSetChanged();

        swipeRefresh.setRefreshing(false);
    }

    private void goToArrangeScreen() {

        Intent intent = new Intent(getApplicationContext(), ArrangeActivity.class);

        ArrayList<Playlist> selected = new ArrayList<>();
        for(Playlist pl : playlists)
            if (pl.selected)
                selected.add(pl);

        intent.putParcelableArrayListExtra("playlists", selected);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in_hard, R.anim.fade_out_hard);
    }

    private void selectAll(boolean selected) {
        if (this.playlists != null && this.playlistSelectionAdapter != null) {
            for (Playlist pl : this.playlists) {
                pl.selected = selected;
            }
            playlistSelectionAdapter.notifyDataSetChanged();
            updateSelectedPlaylists();
        }
    }

    private void updateSelectedPlaylists() {

        selectedPlaylistIds.clear();

        for (Playlist pl : this.playlists)
            if (pl.selected)
                selectedPlaylistIds.add(pl.id);

        if (selectedPlaylistIds.size() > 0) {
            toolbar.setTitle(String.format("%d / %d valittu", selectedPlaylistIds.size(), this.playlists.size()));
            nextButton.setEnabled(true);
        } else {
            toolbar.setTitle(this.toolbarDefaultTitle);
            nextButton.setEnabled(false);
        }
    }
}