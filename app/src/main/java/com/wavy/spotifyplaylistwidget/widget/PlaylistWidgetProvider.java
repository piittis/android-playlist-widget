package com.wavy.spotifyplaylistwidget.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.wavy.spotifyplaylistwidget.R;

public class PlaylistWidgetProvider extends AppWidgetProvider {

    private static final String TAG = "PlaylistWidgetProvider";


    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

        // PlaylistWidgetService will provide and instance of PlaylistsViewsFactory
        Intent intent = new Intent(context, PlaylistWidgetService.class);

        views.setRemoteAdapter(R.id.widget_list, intent);

        // Tell the AppWidgetManager to perform an update on the current app widget
        appWidgetManager.updateAppWidget(appWidgetId, views);

        // todo listen to clicks and open playlists like so:
        // Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("spotify:user:piittis2:playlist:5PFpnK4yLyIlRZW8jEJXir"));
        // startActivity(i);
    }


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }

        Log.d(TAG, "onUpdate");
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {

        // When the user deletes the widget, delete the preference associated with it.
       /* final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            SpotiWidgetConfigureActivity.deleteTitlePref(context, appWidgetIds[i]);
        }*/
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

}
