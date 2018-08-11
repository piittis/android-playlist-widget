package com.wavy.spotifyplaylistwidget;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.crashlytics.android.Crashlytics;
import com.wavy.spotifyplaylistwidget.db.AppDatabase;
import com.wavy.spotifyplaylistwidget.db.entity.PlaylistEntity;
import com.wavy.spotifyplaylistwidget.db.entity.WidgetEntity;
import com.wavy.spotifyplaylistwidget.db.entity.WidgetOptions;
import com.wavy.spotifyplaylistwidget.db.entity.WidgetPlaylist;
import com.wavy.spotifyplaylistwidget.listAdapters.PlaylistArrangeAdapter;
import com.wavy.spotifyplaylistwidget.persistence.WidgetConfigFileRepository;
import com.wavy.spotifyplaylistwidget.utils.FileHelper;
import com.wavy.spotifyplaylistwidget.viewModels.PlaylistViewModel;
import com.wavy.spotifyplaylistwidget.widget.PlaylistModel;
import com.wavy.spotifyplaylistwidget.widget.WidgetConfigModel;

import org.threeten.bp.Instant;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class ArrangeActivity extends PlaylistWidgetConfigureActivityBase {

    // view elements
    @BindView(R.id.playlist_arrange_list) RecyclerView mPlaylistArrangeView;
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

        mNextButton.setOnClickListener((v) -> addWidget());
    }

    @SuppressLint("CheckResult")
    private void addWidget() {

        findViewById(R.id.arrange_activity_elements).setVisibility(View.GONE);
        findViewById(R.id.processing_indicator).setVisibility(View.VISIBLE);

        Completable.concatArray(
                mFileHelper.persistPlaylistImages(this, mPlaylists.getSelectedPlaylists()),
                this.saveWidgetConfig())
        .subscribeOn(AndroidSchedulers.mainThread())
        .subscribe(() -> {
            logEvent("new_widget_created");
            finishWidgetConfiguration();
        }, e -> {
            Crashlytics.log("Error saving widget information");
            Crashlytics.logException(e);
            quitWithError(e.getMessage());
        });
    }

    // TODO: is there a better place for this logic?
    @SuppressLint("CheckResult")
    private Completable saveWidgetConfig() {

        // Create entities.
        ArrayList<PlaylistEntity> playlists = new ArrayList<>();
        ArrayList<WidgetPlaylist> widgetplaylists = new ArrayList<>();

        int position = 1;
        for (PlaylistViewModel pl : mPlaylists.getSelectedPlaylists()) {
            playlists.add(new PlaylistEntity(pl.id, pl.name, pl.uri, pl.owner, pl.tracks));
            widgetplaylists.add(new WidgetPlaylist(mAppWidgetId, pl.id, position));
            position++;
        }

        WidgetEntity widgetEntity = new WidgetEntity(mAppWidgetId, Instant.now(), WidgetOptions.getDefaultOptions());

        return Completable.fromRunnable(() -> {
            // Persist them
            mAppDatabase.beginTransaction();
            try {
                mAppDatabase.widgetDao().upsert(widgetEntity);
                mAppDatabase.playlistDao().upsertAll(playlists);
                mAppDatabase.widgetPlaylistDao().setWidgetsPlaylists(mAppWidgetId, widgetplaylists);
                mAppDatabase.setTransactionSuccessful();
            }
            finally {
                mAppDatabase.endTransaction();
            }
        });
    }

}