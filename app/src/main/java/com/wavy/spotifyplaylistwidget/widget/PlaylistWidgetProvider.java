package com.wavy.spotifyplaylistwidget.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.wavy.spotifyplaylistwidget.IoC;
import com.wavy.spotifyplaylistwidget.R;
import com.wavy.spotifyplaylistwidget.SelectActivity;
import com.wavy.spotifyplaylistwidget.db.AppDatabase;
import com.wavy.spotifyplaylistwidget.db.entity.WidgetEntity;

import javax.inject.Inject;

/**
 * See documentation for this stuff:
 * https://developer.android.com/guide/topics/appwidgets/index.html#collections
 */
public class PlaylistWidgetProvider extends AppWidgetProvider {

    private static final String TAG = "PlaylistWidgetProvider";
    public static final String OPEN_PLAYLIST_ACTION = "com.wavy.spotifyplaylistwidget.OPEN_PLAYLIST";

    @Inject
    AppDatabase mAppDatabase;

    public PlaylistWidgetProvider() {
        super();

        IoC.getInjector().inject(this);
    }

    /**
     * Call to setup the widget.
     */
    public static void InitializeWidget(WidgetEntity widget, Context context) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

        // Set up the intent that starts the PlaylistWidgetService, which will
        // provide the views for this collection.
        Intent intent = new Intent(context, PlaylistWidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widget.androidWidgetId);
        // Purpose of this is to differentiate the intents (filterEquals must return false).
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

        int color = Color.parseColor(widget.options.backgroundColor);
        int alpha = (int)((float)widget.options.backgroundOpacity / 100f * 255f);

        // First byte of color is the alpha, zero out the alpha portion and write new alpha.
        color &= (0x00ffffff);
        color |= (alpha << 24);
        views.setInt(R.id.widget_container, "setBackgroundColor", color);

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

        // Tell the AppWidgetManager to perform an update on the current app widget.
        AppWidgetManager.getInstance(context).updateAppWidget(widget.androidWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        // There may be multiple widgets active, so update all of them.
        for (int appWidgetId : appWidgetIds) {
            WidgetEntity widget = mAppDatabase.widgetDao().getById(appWidgetId);
            if (widget != null) {
                InitializeWidget(widget, context);
            }
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {

        // When the user deletes the widget, delete related data.
        for (int appWidgetId : appWidgetIds) {
            logEvent(context, "widget_remove");
            mAppDatabase.widgetDao().deleteById(appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created.
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled.
        // This seems like a good spot to nuke the db.
        mAppDatabase.clearAllTables();
    }

    // Called when the BroadcastReceiver receives an Intent broadcast.
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(OPEN_PLAYLIST_ACTION)) {
            if (!spotifyInstalled(context)) {
                Toast.makeText(context, "Spotify app not installed", Toast.LENGTH_LONG).show();
                return;
            }

            try {
                if (intent.getBooleanExtra("edit", false)) {
                    int widgetId = intent.getIntExtra("widgetId", 0);
                    Intent i = new Intent(context, SelectActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
                    context.startActivity(i);
                    logEvent(context, "playlist_edit_click");
                } else {
                    // Opens the given playlist.
                    String uri = intent.getStringExtra("uri");
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);
                    logEvent(context, "playlist_open");
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Toast.makeText(context, "Can't open playlist (" + e.getMessage() + ")", Toast.LENGTH_LONG).show();
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

    private void logEvent(Context context, String event) {
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        mFirebaseAnalytics.logEvent(event, new Bundle());
    }
}