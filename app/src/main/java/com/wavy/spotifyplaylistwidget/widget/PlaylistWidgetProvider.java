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

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them

        final int N = appWidgetIds.length;
        for (int appWidgetId : appWidgetIds) {

            updateAppWidget(context, appWidgetManager, appWidgetId);

            // Create an Intent to launch ExampleActivity
            //Intent intent = new Intent(context, ExampleActivity.class);
            //PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            // Get the layout for the App Widget and attach an on-click listener
            // to the button
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            //views.setOnClickPendingIntent(R.id.button, pendingIntent);

            Intent intent = new Intent(context, PlaylistWidgetService.class);
            views.setRemoteAdapter(R.id.widget_list, intent);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
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

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        //CharSequence widgetText = SpotiWidgetConfigureActivity.loadTitlePref(context, appWidgetId);
        // Construct the RemoteViews object
        //RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.spoti_widget);
        //views.setTextViewText(R.id.appwidget_text, widgetText);

        // Instruct the widget manager to update the widget
       // appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}
