package com.wavy.spotifyplaylistwidget.widget;

import android.content.Context;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.wavy.spotifyplaylistwidget.R;
import com.wavy.spotifyplaylistwidget.viewModels.PlaylistViewModel;

import java.util.ArrayList;

public class PlaylistViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private ArrayList<PlaylistViewModel> mPlaylists;
    private Context mContext;

    public PlaylistViewsFactory(Context context) {

        // todo get from real place
        mPlaylists = PlaylistViewModel.getListForDebug();
        mContext = context;

    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return mPlaylists.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {

        final RemoteViews removeView = new RemoteViews(mContext.getPackageName(), R.layout.widget_playlist);
        PlaylistViewModel pl = mPlaylists.get(position);
        removeView.setTextViewText(R.id.playlist_name, pl.name);

        //todo load bitmap
        removeView.setImageViewResource(R.id.playlist_image, R.drawable.ic_music_note_white_24dp);

        return removeView;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
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
