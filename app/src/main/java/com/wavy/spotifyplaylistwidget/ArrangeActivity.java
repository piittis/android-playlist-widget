package com.wavy.spotifyplaylistwidget;

import android.os.Bundle;
import android.widget.Button;

import com.mobeta.android.dslv.DragSortListView;
import com.wavy.spotifyplaylistwidget.listAdapters.PlaylistArrangeAdapter;
import com.wavy.spotifyplaylistwidget.viewModels.PlaylistViewModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ArrangeActivity extends PlaylistWidgetConfigureActivityBase {

    private ArrayList<PlaylistViewModel> mPlaylists;

    // view elements
    @BindView(R.id.playlist_arrange_list) DragSortListView mPlaylistArrangeView;
    @BindView(R.id.arrage_next_button) Button mNextButton;

    private PlaylistArrangeAdapter mPlaylistArrangeAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arrange);

        /*this.getWindow().setLayout(WindowSizeHelper.getWindowWidthPx(this),
                WindowSizeHelper.getWindowHeight(this));*/

        ButterKnife.bind(this);

        mPlaylists = getIntent().getParcelableArrayListExtra("mPlaylists");

        mPlaylistArrangeAdapter = new PlaylistArrangeAdapter(this, R.layout.arrangeable_playlist, mPlaylists);
        mPlaylistArrangeView.setAdapter(mPlaylistArrangeAdapter);

        mPlaylistArrangeView.setDropListener(onDrop);

        mPlaylistArrangeView.setDragEnabled(true);

        mNextButton.setOnClickListener((v) -> addWidget());
    }


    private DragSortListView.DropListener onDrop =
            new DragSortListView.DropListener() {
                @Override
                public void drop(int from, int to) {
                    if (from != to) {
                        PlaylistViewModel item = mPlaylistArrangeAdapter.getItem(from);
                        mPlaylistArrangeAdapter.remove(item);
                        mPlaylistArrangeAdapter.insert(item, to);
                    }
                }
            };

    private void addWidget() {
        //todo implement
        finishWidgetConfiguration();
    }

}
