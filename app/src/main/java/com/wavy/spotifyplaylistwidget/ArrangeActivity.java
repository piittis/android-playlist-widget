package com.wavy.spotifyplaylistwidget;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.mobeta.android.dslv.DragSortListView;
import com.wavy.spotifyplaylistwidget.listAdapters.PlaylistArrangeAdapter;
import com.wavy.spotifyplaylistwidget.models.Playlist;

import java.util.ArrayList;

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
