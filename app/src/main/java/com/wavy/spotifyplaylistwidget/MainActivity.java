package com.wavy.spotifyplaylistwidget;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ListView;

import com.wavy.spotifyplaylistwidget.listAdapters.PlaylistSelectionAdapter;
import com.wavy.spotifyplaylistwidget.models.Playlist;
import com.wavy.spotifyplaylistwidget.network.SpotifyApi;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements SpotifyApi.playlistsLoadedCallbackListener {

    private ArrayList<Playlist> playlists;
    private RecyclerView playlistsSelectionView;
    private PlaylistSelectionAdapter playlistSelectionAdapter;
    private Toolbar toolbar;
    final String toolbarDefaultTitle = "Select playlists";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.selection_toolbar);
        toolbar.setTitle(toolbarDefaultTitle);
        setSupportActionBar(toolbar);

        new SpotifyApi().getPlaylists(this);
    }

    @Override
    public void onPlaylistsLoaded(ArrayList<Playlist> playlists) {

        this.playlists = playlists;
        this.playlistSelectionAdapter = new PlaylistSelectionAdapter(playlists);
        this.playlistsSelectionView = (RecyclerView)this.findViewById(R.id.playlist_selection_list);

        playlistsSelectionView.setAdapter(this.playlistSelectionAdapter);
        playlistsSelectionView.setLayoutManager(new LinearLayoutManager(this));

        playlistSelectionAdapter.setOnClickListener((v) -> {
            updateSelectedCount();
        });
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
                selectAll();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private void selectAll() {
        if (this.playlists != null && this.playlistSelectionAdapter != null) {
            for (Playlist pl : this.playlists) {
                pl.selected = true;
            }
            playlistSelectionAdapter.notifyDataSetChanged();
            updateSelectedCount();
        }
    }

    private void updateSelectedCount() {

        int selected = 0;
        for (Playlist pl : this.playlists)
            if (pl.selected) selected++;

        if (selected > 0) {
            toolbar.setTitle(String.format("%d / %d valittu", selected, this.playlists.size()));
        } else {
            toolbar.setTitle(this.toolbarDefaultTitle);
        }
    }


}
