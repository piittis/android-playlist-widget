package com.wavy.spotifyplaylistwidget.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.wavy.spotifyplaylistwidget.R;
import com.wavy.spotifyplaylistwidget.persistence.WidgetConfigFileRepository;

/**
 * See documentation for this stuff:
 * https://developer.android.com/guide/topics/appwidgets/index.html#collections
 */
public class PlaylistWidgetProvider extends AppWidgetProvider {

    private static final String TAG = "PlaylistWidgetProvider";
    public static final String OPEN_PLAYLIST_ACTION = "com.wavy.spotifyplaylistwidget.TOAST_ACTION";

    public static void updateWidgetId(Context context, int appWidgetId) {
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        updateAppWidget(context, manager, appWidgetId);
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        Log.d(TAG, "update " + appWidgetId);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

        // Set up the intent that starts the StackViewService, which will
        // provide the views for this collection.
        Intent intent = new Intent(context, PlaylistWidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        // Purpose of this is to differentiate the intents (filterEquals must return false).
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

        views.setRemoteAdapter(R.id.widget_list, intent);

        // This section makes it possible for items to have individualized behavior.
        // It does this by setting up a pending intent template. Individuals items of a collection
        // cannot set up their own pending intents. Instead, the collection as a whole sets
        // up a pending intent template, and the individual items set a fillInIntent
        // to create unique behavior on an item-by-item basis.
        Intent clickIntent = new Intent(context, PlaylistWidgetProvider.class);

        // Set the action for the intent.
        // When the user touches a particular view, it will have the effect of
        // broadcasting OPEN_PLAYLIST_ACTION.
        clickIntent.setAction(OPEN_PLAYLIST_ACTION);

        PendingIntent clickPendingIntent = PendingIntent.getBroadcast(context, 0, clickIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        views.setPendingIntentTemplate(R.id.widget_list, clickPendingIntent);

        // Tell the AppWidgetManager to perform an update on the current app widget
        appWidgetManager.updateAppWidget(appWidgetId, views);

    }


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {

        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            new WidgetConfigFileRepository(context).remove(appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    // Called when the BroadcastReceiver receives an Intent broadcast.
    // Checks to see whether the intent's action is TOAST_ACTION. If it is, the app widget
    // displays a Toast message for the current item.
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(OPEN_PLAYLIST_ACTION)) {

            if (spotifyInstalled(context)) {
                // Opens the given playlist.
                String uri = intent.getStringExtra("uri");
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            } else {
                Toast.makeText(context, "Please install the Spotify app", Toast.LENGTH_LONG).show();
            }

        }

        super.onReceive(context, intent);
    }

    private boolean spotifyInstalled(Context context) {
        try {
            context.getPackageManager().getPackageInfo("com.spotify.music", 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

}
