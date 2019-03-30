package com.wavy.spotifyplaylistwidget;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.wavy.spotifyplaylistwidget.db.AppDatabase;
import com.wavy.spotifyplaylistwidget.db.entity.WidgetPlaylist;
import com.wavy.spotifyplaylistwidget.listAdapters.PlaylistArrangeAdapter;
import com.wavy.spotifyplaylistwidget.utils.FileHelper;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ArrangeActivity extends PlaylistWidgetConfigureActivityBase {

    // view elements
    @BindView(R.id.playlist_arrange_list)
    RecyclerView mPlaylistArrangeView;
    @BindView(R.id.arrage_next_button) Button mNextButton;

    @Inject
    AppDatabase mAppDatabase;

    @Inject
    FileHelper mFileHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IoC.getInjector().inject(this);

        setContentView(R.layout.activity_arrange);
        ButterKnife.bind(this);

        if (isFirstCreate) {
            initializeFromDb();
        }

        PlaylistArrangeAdapter mPlaylistArrangeAdapter =
                new PlaylistArrangeAdapter(mPlaylistArrangeView, mPlaylists.getSelectedPlaylists(), this);
        mPlaylistArrangeView.setAdapter(mPlaylistArrangeAdapter);
        mPlaylistArrangeView.setLayoutManager(new LinearLayoutManager(this));

        mNextButton.setOnClickListener((v) -> {
            Intent intent = new Intent(getApplicationContext(), CustomizeActivity.class);
            startNextConfigurationActivity(intent);
        });
    }

    private void initializeFromDb() {
        List<WidgetPlaylist> existing = mAppDatabase.widgetPlaylistDao().getByWidgetId(mAppWidgetId);
        if (existing == null)
            return;

        HashMap<String, Integer> playlistPosition = new HashMap<>();
        for (WidgetPlaylist pl : existing) {
            playlistPosition.put(pl.playlistId, pl.playlistPosition);
        }

        Collections.sort(mPlaylists.getSelectedPlaylists(),
                (a, b) ->
                {
                    Integer aPos = playlistPosition.get(a.id);
                    Integer bPos = playlistPosition.get(b.id);
                    aPos = aPos == null ? Integer.MAX_VALUE : aPos;
                    bPos = bPos == null ? Integer.MAX_VALUE : bPos;
                    return aPos - bPos;
                });

    }

}