package com.wavy.spotifyplaylistwidget.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.squareup.picasso.Picasso;
import com.wavy.spotifyplaylistwidget.IoC;
import com.wavy.spotifyplaylistwidget.R;
import com.wavy.spotifyplaylistwidget.db.AppDatabase;
import com.wavy.spotifyplaylistwidget.db.entity.PlaylistEntity;
import com.wavy.spotifyplaylistwidget.db.entity.WidgetEntity;
import com.wavy.spotifyplaylistwidget.persistence.WidgetConfigFileRepository;
import com.wavy.spotifyplaylistwidget.persistence.WidgetConfigRepository;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

public class PlaylistViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private static final String TAG = "PlaylistViewsFactory";

    private Context mContext;
    private int mAppWidgetId;
    private int mItemCount;
    private String mTrackCountString;
    private String mImageBasePath;
    private Boolean Error = false;
    private RemoteViews mLoadingView;

    private WidgetEntity mWidget;
    private List<PlaylistEntity> mPlaylists;

    private int mPrimaryTextColor;
    private int mSecondaryTetxtColor;

    @Inject
    AppDatabase mAppDatabase;

    public PlaylistViewsFactory(Context context, Intent intent) {
        mContext = context;
        mImageBasePath = mContext.getFilesDir().getAbsolutePath() + File.separator;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        mTrackCountString = context.getString(R.string.track_count);
        mLoadingView = new RemoteViews(mContext.getPackageName(), R.layout.widget_playlist_loading_placeholder);
    }

    @Override
    public void onCreate() {
        IoC.getInjector().inject(this);

        try {

            mWidget = mAppDatabase.widgetDao().getById(mAppWidgetId);
            mPlaylists = mAppDatabase.widgetPlaylistDao().getWidgetPlaylists(mAppWidgetId);
            mItemCount = mPlaylists.size();

            mPrimaryTextColor = Color.parseColor(mWidget.options.primaryTextColor);
            mSecondaryTetxtColor = Color.parseColor(mWidget.options.secondaryTextColor);

        } catch(Exception e) {
            mItemCount = 1;
            Error = true;
        }

    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return mItemCount;
    }

    @Override
    public RemoteViews getViewAt(int position) {

        if (Error) {
            return new RemoteViews(mContext.getPackageName(), R.layout.widget_error);
        }

        final RemoteViews remoteView = new RemoteViews(mContext.getPackageName(), R.layout.widget_playlist);

        PlaylistEntity pl = mPlaylists.get(position);

        remoteView.setTextViewText(R.id.playlist_name, pl.name);
        remoteView.setTextColor(R.id.playlist_name, mPrimaryTextColor);

        remoteView.setTextViewText(R.id.playlist_info, String.format(mTrackCountString, pl.trackCount));
        remoteView.setTextColor(R.id.playlist_info, mSecondaryTetxtColor);

        try {
            Bitmap map = Picasso.get()
                    .load(new File(mImageBasePath +  pl.spotifyId + ".png"))
                    .error(R.drawable.ic_music_note_white_48dp)
                    .get();
            remoteView.setImageViewBitmap(R.id.playlist_image, map);

        } catch (Exception e) {
            e.printStackTrace();
            remoteView.setImageViewResource(R.id.playlist_image, R.drawable.ic_music_note_white_48dp);
        }

        Intent fillInIntent = new Intent();
        fillInIntent.putExtra("uri", pl.uri);
        remoteView.setOnClickFillInIntent(R.id.widget_playlist_item, fillInIntent);

        return remoteView;
    }

    @Override
    public RemoteViews getLoadingView() {
        return mLoadingView;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
