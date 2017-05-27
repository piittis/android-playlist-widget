package com.wavy.spotifyplaylistwidget.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class PlaylistWidgetService extends RemoteViewsService {


    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new PlaylistViewsFactory(getApplicationContext());
    }
}
