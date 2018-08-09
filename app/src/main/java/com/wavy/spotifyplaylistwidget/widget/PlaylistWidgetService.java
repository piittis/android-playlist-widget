package com.wavy.spotifyplaylistwidget.widget;

import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViewsService;

public class PlaylistWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Log.d("PlaylistWidgetService", "onGetViewFactory");
        return new PlaylistViewsFactory(getApplicationContext(), intent);
    }
}
