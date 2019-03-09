package com.wavy.spotifyplaylistwidget;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.wavy.spotifyplaylistwidget.db.AppDatabase;
import com.wavy.spotifyplaylistwidget.listAdapters.PlaylistArrangeAdapter;
import com.wavy.spotifyplaylistwidget.utils.FileHelper;

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

        PlaylistArrangeAdapter mPlaylistArrangeAdapter =
                new PlaylistArrangeAdapter(mPlaylistArrangeView, mPlaylists.getSelectedPlaylists(), this);
        mPlaylistArrangeView.setAdapter(mPlaylistArrangeAdapter);
        mPlaylistArrangeView.setLayoutManager(new LinearLayoutManager(this));

        mNextButton.setOnClickListener((v) -> {
            Intent intent = new Intent(getApplicationContext(), CustomizeActivity.class);
            startNextConfigurationActivity(intent);
        });
    }

}