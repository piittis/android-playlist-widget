package com.wavy.spotifyplaylistwidget;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Button;

import com.wavy.spotifyplaylistwidget.listAdapters.PlaylistSelectionAdapter;
import com.wavy.spotifyplaylistwidget.models.Playlist;
import com.wavy.spotifyplaylistwidget.network.SpotifyApi;

import java.util.ArrayList;
import java.util.HashSet;

public class ArrangeActivity extends AppCompatActivity {

    private ArrayList<Playlist> playlists;

    // view elements
    /*private RecyclerView playlistsSelectionView;
    private PlaylistSelectionAdapter playlistSelectionAdapter;
    private Button nextButton;
    private Toolbar toolbar;
    private SwipeRefreshLayout swipeRefresh;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arrange);

        playlists = getIntent().getParcelableArrayListExtra("playlists");

        int i = 0 ;

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in_hard, R.anim.fade_out_hard);
    }
}
