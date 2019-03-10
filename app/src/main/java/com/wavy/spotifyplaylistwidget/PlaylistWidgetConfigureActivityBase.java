package com.wavy.spotifyplaylistwidget;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.wavy.spotifyplaylistwidget.db.AppDatabase;
import com.wavy.spotifyplaylistwidget.db.entity.PlaylistEntity;
import com.wavy.spotifyplaylistwidget.db.entity.WidgetEntity;
import com.wavy.spotifyplaylistwidget.db.entity.WidgetOptions;
import com.wavy.spotifyplaylistwidget.db.entity.WidgetPlaylist;
import com.wavy.spotifyplaylistwidget.utils.FileHelper;
import com.wavy.spotifyplaylistwidget.viewModels.PlaylistViewModel;
import com.wavy.spotifyplaylistwidget.widget.PlaylistWidgetProvider;

import org.threeten.bp.Instant;

import java.util.ArrayList;

import javax.inject.Inject;

import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Since the widget configuration might span multiple activities, all activities in the
 * widget configuration flow should extend this class. This will handle propagating the appWidgetId
 * and playlists across the activities. When some activity decides that widget creation/updating is
 * done, it should call finishWidgetConfiguration. That will unwind all the activities.
 * Last activity to be finished will update the widget.
 */
public abstract class PlaylistWidgetConfigureActivityBase extends AppCompatActivity {

    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private static final int RESULT_CONFIGURATION_DONE = 1234;
    private static final int RESULT_CONFIGURATION_CANCELLED = 2345;
    protected static final int AUTH_REQUEST = 99;
    protected static final int CHILD_ACTIVITY = 199;
    private static final String TAG = "ConfigureBaseClass";
    private Boolean mHasParent = false;
    protected Boolean mIsAuthenticating = false;
    // We might want to do something the first time activity is opened, but not after that.
    protected Boolean isFirstCreate = true;
    // We might want to prevent certain (async) operations from finishing if activity is being stopped.
    protected Boolean activityStopped = false;

    private FirebaseAnalytics mFirebaseAnalytics;

    @Inject
    PlaylistsContainer mPlaylists;

    @Inject
    AppDatabase mAppDatabase;

    @Inject
    FileHelper mFileHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        IoC.getInjector().inject(this);
        super.onCreate(savedInstanceState);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        if (savedInstanceState != null) {
            isFirstCreate = savedInstanceState.getBoolean("isFirstCreate", true);
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
        if (!BuildConfig.DEBUG && mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            quitWithError("invalid widget id");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        activityStopped = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        activityStopped = true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isAuthenticating", mIsAuthenticating);
        outState.putBoolean("isFirstCreate", false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == CHILD_ACTIVITY) {
            if (resultCode == RESULT_CONFIGURATION_DONE) {
                // Child activity finished because widget configuration is finished.
                // Finish this activity also.
                finishWidgetConfiguration();
            } else if (resultCode == RESULT_CONFIGURATION_CANCELLED) {
                cancelWidgetConfiguration();
            }
        }
        else if (requestCode == AUTH_REQUEST) {
            mIsAuthenticating = false;
            if (resultCode == RESULT_CANCELED) {
                // Could not authenticate, no point in continuing with the configuration.
                cancelWidgetConfiguration();
            }
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in_hard, R.anim.fade_out_hard);
    }

    protected void quitWithError(String reason) {
        this.setResult(RESULT_CANCELED);
        Toast.makeText(getApplicationContext(), getString(R.string.configuration_error) + " (" + reason + ")", Toast.LENGTH_LONG).show();
        cancelWidgetConfiguration();
    }

    /**
     * Starts the authentication activity if not started already.
     * Extending activity can react to authentication finishing in its onActivityResult.
     */
    protected void doAuthentication() {
        if (mIsAuthenticating)
            return;

        // Authenticate and return back to this activity.
        Intent intent = new Intent(getApplicationContext(), AuthActivity.class);
        mIsAuthenticating = true;
        startActivityForResult(intent, AUTH_REQUEST);
    }

    protected void startNextConfigurationActivity(Intent intent) {
        intent.putExtra("hasParent", true);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        startActivityForResult(intent, CHILD_ACTIVITY);
        overridePendingTransition(R.anim.fade_in_hard, R.anim.fade_out_hard);
    }

    /**
     * finishAndRemoveTask() is api level >= 21. FinishAffinity causes a crash.
     * Thats why when quitting, each activity has to finish one after the other in this way.
     */
    protected void cancelWidgetConfiguration() {
        if (mHasParent) {
            // We have a parent activity, let it handle it.
            setResult(RESULT_CONFIGURATION_CANCELLED);
            finish();
        } else {
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_CANCELED, resultValue);

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                super.finishAndRemoveTask();
                return;
            }
        }

        finish();
    }

    @SuppressLint("CheckResult")
    protected void addWidget(WidgetOptions options) {

        Completable.concatArray(
                mFileHelper.persistPlaylistImages(this, mPlaylists.getSelectedPlaylists()),
                this.saveWidgetConfig(options))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    logEvent("new_widget_created");
                    finishWidgetConfiguration();
                }, e -> {
                    Crashlytics.log("Error saving widget information");
                    Crashlytics.logException(e);
                    quitWithError(e.getMessage());
                });
    }

    @SuppressLint("CheckResult")
    protected Completable saveWidgetConfig(WidgetOptions options) {

        // Create entities.
        ArrayList<PlaylistEntity> playlists = new ArrayList<>();
        ArrayList<WidgetPlaylist> widgetplaylists = new ArrayList<>();

        int position = 1;
        for (PlaylistViewModel pl : mPlaylists.getSelectedPlaylists()) {
            playlists.add(new PlaylistEntity(pl.id, pl.name, pl.uri, pl.owner, pl.tracks));
            widgetplaylists.add(new WidgetPlaylist(mAppWidgetId, pl.id, position));
            position++;
        }

        WidgetEntity widgetEntity = new WidgetEntity(mAppWidgetId, Instant.now(), options != null ? options : WidgetOptions.getDefaultOptions());

        return Completable.fromRunnable(() -> {
            // Persist them
            mAppDatabase.beginTransaction();
            try {
                mAppDatabase.widgetDao().upsert(widgetEntity);
                mAppDatabase.playlistDao().upsertAll(playlists);
                mAppDatabase.widgetPlaylistDao().setWidgetsPlaylists(mAppWidgetId, widgetplaylists);
                mAppDatabase.setTransactionSuccessful();
            }
            finally {
                mAppDatabase.endTransaction();
            }
        });
    }

    /**
     * To support arbitrary amount of configuration activities, the last activity should call this
     * when it decides configuration is done.
     */
    protected void finishWidgetConfiguration() {

        if (mHasParent) {
            // We have a parent activity, let it handle it.
            setResult(RESULT_CONFIGURATION_DONE);
        } else {
            // No parent to return to, set RESULT_OK.
            updateWidget();
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                super.finishAndRemoveTask();
                return;
            }
        }

        finish();
    }

    private void updateWidget() {

        // Send widget update broadcast, it is handled in PlaylistWidgetProvider
        Intent updateIntent = new Intent(this, PlaylistWidgetProvider.class);
        updateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[]{ mAppWidgetId });
        sendBroadcast(updateIntent);
    }

    protected void logEvent(String event) {
        mFirebaseAnalytics.logEvent(event, new Bundle());
    }

    // Makes toasts when debugging.
    protected void dToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }
    protected void dToast(int num) {
        this.dToast(Integer.toString(num));
    }
}