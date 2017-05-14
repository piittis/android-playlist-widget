package com.wavy.spotifyplaylistwidget;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.ListView;

import com.mobeta.android.dslv.DragSortListView;
import com.wavy.spotifyplaylistwidget.listAdapters.PlaylistArrangeAdapter;
import com.wavy.spotifyplaylistwidget.listAdapters.PlaylistSelectionAdapter;
import com.wavy.spotifyplaylistwidget.models.Playlist;
import com.wavy.spotifyplaylistwidget.network.SpotifyApi;

import java.util.ArrayList;
import java.util.HashSet;

public class ArrangeActivity extends AppCompatActivity {

    private ArrayList<Playlist> playlists;

    // view elements
    private DragSortListView playlistArrangeView;
    private PlaylistArrangeAdapter playlistArrangeAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arrange);

        playlists = getIntent().getParcelableArrayListExtra("playlists");

        playlistArrangeView = (DragSortListView)this.findViewById(R.id.playlist_arrange_list);
        playlistArrangeAdapter = new PlaylistArrangeAdapter(this, R.layout.arrangeable_playlist, playlists);
        playlistArrangeView.setAdapter(playlistArrangeAdapter);

        playlistArrangeView.setDropListener(onDrop);

        playlistArrangeView.setDragEnabled(true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in_hard, R.anim.fade_out_hard);
    }

    private DragSortListView.DropListener onDrop =
            new DragSortListView.DropListener() {
                @Override
                public void drop(int from, int to) {
                    if (from != to) {
                        Playlist item = playlistArrangeAdapter.getItem(from);
                        playlistArrangeAdapter.remove(item);
                        playlistArrangeAdapter.insert(item, to);
                    }
                }
            };

}
