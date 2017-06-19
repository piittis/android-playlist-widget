package com.wavy.spotifyplaylistwidget.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.wavy.spotifyplaylistwidget.R;
import com.wavy.spotifyplaylistwidget.persistence.WidgetConfigFileRepository;
import com.wavy.spotifyplaylistwidget.persistence.WidgetConfigRepository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class PlaylistViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private static final String TAG = "PlaylistViewsFactory";
    private WidgetConfigModel mWidgetConfig;

    private Context mContext;
    private int mAppWidgetId;
    private int mItemCount;
    private WidgetConfigRepository mConfigRepository;
    private String mTrackCountString;
    private Boolean Error = false;
    private RemoteViews mLoadingView;

    public PlaylistViewsFactory(Context context, Intent intent) {
        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        mConfigRepository = new WidgetConfigFileRepository(mContext);
        mTrackCountString = context.getString(R.string.track_count);
        mLoadingView = new RemoteViews(mContext.getPackageName(), R.layout.widget_playlist_loading_placeholder);
    }

    @Override
    public void onCreate() {
        try {
            mWidgetConfig = mConfigRepository.get(mAppWidgetId);
            mItemCount = mWidgetConfig.getPlaylists().size();
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
        PlaylistModel pl = mWidgetConfig.getPlaylists().get(position);
        remoteView.setTextViewText(R.id.playlist_name, pl.name);
        remoteView.setTextViewText(R.id.playlist_info, String.format(mTrackCountString, pl.tracks));

        try {
            Bitmap map = Picasso.with(mContext)
                    .load(new File(mContext.getFilesDir().getAbsolutePath() + File.separator +  pl.id + ".png"))
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
