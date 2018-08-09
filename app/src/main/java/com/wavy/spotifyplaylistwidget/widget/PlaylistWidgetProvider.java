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

import com.google.firebase.analytics.FirebaseAnalytics;
import com.wavy.spotifyplaylistwidget.IoC;
import com.wavy.spotifyplaylistwidget.R;
import com.wavy.spotifyplaylistwidget.db.AppDatabase;
import com.wavy.spotifyplaylistwidget.db.entity.PlaylistEntity;
import com.wavy.spotifyplaylistwidget.db.entity.WidgetEntity;
import com.wavy.spotifyplaylistwidget.db.entity.WidgetOptions;
import com.wavy.spotifyplaylistwidget.db.entity.WidgetPlaylist;
import com.wavy.spotifyplaylistwidget.persistence.WidgetConfigFileRepository;

import org.threeten.bp.Instant;

import java.util.ArrayList;

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

        views.setInt(R.id.widget_container, "setBackgroundColor", Color.parseColor(widget.options.backgroundColor));

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

            migrateToSqlite(context, appWidgetId);

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

            if (spotifyInstalled(context)) {

                try {
                    // Opens the given playlist.
                    String uri = intent.getStringExtra("uri");
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);
                    logEvent(context, "playlist_open");
                } catch(Exception e) {
                    Toast.makeText(context, "Can't open playlist (" + e.getMessage() + ")", Toast.LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(context, "Spotify app not installed", Toast.LENGTH_LONG).show();
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

    /**
     * upgrade previously used config file to sqlite TODO: remove when all users are on sqlite.
     */
    private void migrateToSqlite(Context context, int appWidgetId) {

        WidgetEntity widget = mAppDatabase.widgetDao().getById(appWidgetId);

        // Widget already in sqlite.
        if (widget != null)
            return;

        WidgetConfigFileRepository configRepo = new WidgetConfigFileRepository(context);
        WidgetConfigModel widgetConfig;

        try {
            widgetConfig = configRepo.get(appWidgetId);
        } catch (Exception e) {
            return;
        }

        if (widgetConfig == null)
            return;

        // Create entities.
        ArrayList<PlaylistEntity> playlists = new ArrayList<>();
        ArrayList<WidgetPlaylist> widgetplaylists = new ArrayList<>();

        WidgetEntity widgetEntity = new WidgetEntity(appWidgetId, Instant.now(), WidgetOptions.getDefaultOptions());

        int position = 1;
        for (PlaylistModel pl : widgetConfig.getPlaylists()) {
            playlists.add(new PlaylistEntity(pl.id, pl.name, pl.uri, pl.owner, pl.tracks));
            widgetplaylists.add(new WidgetPlaylist(appWidgetId, pl.id, position));
            position++;
        }

        // Persist them.
        mAppDatabase.beginTransaction();
        try {
            mAppDatabase.widgetDao().upsert(widgetEntity);
            mAppDatabase.playlistDao().upsertAll(playlists);
            mAppDatabase.widgetPlaylistDao().setWidgetsPlaylists(appWidgetId, widgetplaylists);
            mAppDatabase.setTransactionSuccessful();
        }
        finally {
            mAppDatabase.endTransaction();
            configRepo.remove(appWidgetId);
        }

    }
}
