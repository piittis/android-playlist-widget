package com.wavy.spotifyplaylistwidget;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.mobeta.android.dslv.DragSortListView;
import com.wavy.spotifyplaylistwidget.persistence.FileHelper;
import com.wavy.spotifyplaylistwidget.persistence.WidgetConfigFileRepository;
import com.wavy.spotifyplaylistwidget.persistence.WidgetConfigRepository;
import com.wavy.spotifyplaylistwidget.listAdapters.PlaylistArrangeAdapter;
import com.wavy.spotifyplaylistwidget.viewModels.PlaylistViewModel;
import com.wavy.spotifyplaylistwidget.widget.PlaylistModel;
import com.wavy.spotifyplaylistwidget.widget.WidgetConfigModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ArrangeActivity extends PlaylistWidgetConfigureActivityBase {

    private ArrayList<PlaylistViewModel> mPlaylists;
    private WidgetConfigRepository mWidgetConfigRepository;
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

        mWidgetConfigRepository = new WidgetConfigFileRepository(this);
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

        findViewById(R.id.arrange_activity_elements).setVisibility(View.GONE);
        findViewById(R.id.processing_indicator).setVisibility(View.VISIBLE);

        FileHelper.persistPlaylistImages(this, mPlaylists, () -> {

            WidgetConfigModel newWidgetConfig = new WidgetConfigModel(WidgetConfigModel.TYPE_MULTI);
            ArrayList<PlaylistModel> widgetPlaylists = new ArrayList<>(mPlaylists.size());

            for (PlaylistViewModel pl : mPlaylists) {
                widgetPlaylists.add(new PlaylistModel(pl.name, pl.id, pl.uri, pl.tracks, "", pl.owner));
            }
            newWidgetConfig.setPlaylists(widgetPlaylists);

            mWidgetConfigRepository.put(mAppWidgetId, newWidgetConfig);

            finishWidgetConfiguration();
        });

    }
}