package com.wavy.spotifyplaylistwidget;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.mobeta.android.dslv.DragSortListView;
import com.wavy.spotifyplaylistwidget.listAdapters.PlaylistArrangeAdapter;
import com.wavy.spotifyplaylistwidget.models.Playlist;

import java.util.ArrayList;

public class ArrangeActivity extends AppCompatActivity {

    private ArrayList<Playlist> mPlaylists;

    // view elements
    private DragSortListView mPlaylistArrangeView;
    private PlaylistArrangeAdapter mPlaylistArrangeAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arrange);

        mPlaylists = getIntent().getParcelableArrayListExtra("mPlaylists");

        mPlaylistArrangeView = (DragSortListView)this.findViewById(R.id.playlist_arrange_list);
        mPlaylistArrangeAdapter = new PlaylistArrangeAdapter(this, R.layout.arrangeable_playlist, mPlaylists);
        mPlaylistArrangeView.setAdapter(mPlaylistArrangeAdapter);

        mPlaylistArrangeView.setDropListener(onDrop);

        mPlaylistArrangeView.setDragEnabled(true);
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
                        Playlist item = mPlaylistArrangeAdapter.getItem(from);
                        mPlaylistArrangeAdapter.remove(item);
                        mPlaylistArrangeAdapter.insert(item, to);
                    }
                }
            };

}
