package com.wavy.spotifyplaylistwidget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.wavy.spotifyplaylistwidget.persistence.FileHelper;
import com.wavy.spotifyplaylistwidget.viewModels.PlaylistViewModel;
import com.wavy.spotifyplaylistwidget.widget.PlaylistModel;
import com.wavy.spotifyplaylistwidget.widget.PlaylistWidgetProvider;
import com.wavy.spotifyplaylistwidget.widget.WidgetConfigModel;

import java.util.ArrayList;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;

/**
 * Since the widget configuration might span multiple activities, all activities in the
 * widget configuration flow should extend this class. This will handle propagating the appWidgetId
 * across the activities. When some activity decides that widget creation/updating is done, it should
 * call finishWidgetConfiguration. That will unwind all the activities. Last activity to be finished
 * will update the widget.
 */
public class PlaylistWidgetConfigureActivityBase extends AppCompatActivity {

    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private static final int RESULT_CONFIGURATION_DONE = 1234;
    private static final String TAG = "ConfigureBase";
    private Boolean mHasParent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mHasParent = extras.getBoolean("hasParent", false);
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        if (!mHasParent) {
            // Set the result to CANCELED.  This will cause the widget host to cancel
            // out of the widget placement if the user presses the back button.
            setResult(RESULT_CANCELED);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        // todo quit and show message
        /*if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }*/
    }

    protected void startNextConfigurationActivity(Intent intent) {
        intent.putExtra("hasParent", true);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        startActivityForResult(intent, 199);
    }

    protected void finishWidgetConfiguration() {
        Log.d(TAG, "finishWidgetConfiguration");
        if (mHasParent) {
            // We have a parent activity, let it handle rest.
            setResult(RESULT_CONFIGURATION_DONE);
        } else {
            // No parent to return to, update widged and set RESULT_OK.
            updateWidget();

            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
        }

        finish();
    }

    private void updateWidget() {
        Log.d(TAG, "updateWidget");
        PlaylistWidgetProvider.updateWidgetId(this, mAppWidgetId);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // child activity finished because widget configuration is finished
        // finish this activity also
        if (requestCode == 199 && resultCode == RESULT_CONFIGURATION_DONE) {
            finishWidgetConfiguration();
        }
    }

}