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
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
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
    protected static final int AUTH_REQUEST = 99;
    private static final String TAG = "ConfigureBaseClass";
    private Boolean mHasParent = false;
    protected Boolean mIsAuthenticating = false;

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        if (savedInstanceState != null) {
            mIsAuthenticating = savedInstanceState.getBoolean("isAuthenticating", false);
        }

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mHasParent = extras.getBoolean("hasParent", false);
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // Set result to ok when widged configuration is done succesfully.
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_CANCELED, resultValue);

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            quitWithError("invalid widget id");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isAuthenticating", mIsAuthenticating);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == 199 && resultCode == RESULT_CONFIGURATION_DONE) {
            // Child activity finished because widget configuration is finished.
            // Finish this activity also.
            finishWidgetConfiguration();
        }

        else if (requestCode == AUTH_REQUEST) {
            // Authentication done.
            mIsAuthenticating = false;
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in_hard, R.anim.fade_out_hard);
    }

    protected void quitWithError(String reason) {
        this.setResult(RESULT_CANCELED);
        Toast.makeText(getApplicationContext(), R.string.configuration_error + " (" + reason + ")", Toast.LENGTH_LONG).show();
        finishAffinity();
    }

    /**
     * Starts the authentication activity if not started already.
     * Extending activity can react to authentication finishing in its onActivityResult.
     */
    protected void doAuthentication() {
        if (mIsAuthenticating)
            return;

        Log.d(TAG, "starting authentication");
        // Authenticate and return back to this activity.
        Intent intent = new Intent(getApplicationContext(), AuthActivity.class);
        mIsAuthenticating = true;
        startActivityForResult(intent, AUTH_REQUEST);
    }

    protected void startNextConfigurationActivity(Intent intent) {
        intent.putExtra("hasParent", true);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        startActivityForResult(intent, 199);
        overridePendingTransition(R.anim.fade_in_hard, R.anim.fade_out_hard);
    }

    protected void finishWidgetConfiguration() {

        if (mHasParent) {
            // We have a parent activity, let it handle it.
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

    protected void logEvent(String event) {
        mFirebaseAnalytics.logEvent(event, new Bundle());
    }

}