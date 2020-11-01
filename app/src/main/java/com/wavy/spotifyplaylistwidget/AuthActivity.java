package com.wavy.spotifyplaylistwidget;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;
import com.wavy.spotifyplaylistwidget.exceptions.AuthErrorException;
import com.wavy.spotifyplaylistwidget.exceptions.AuthException;
import com.wavy.spotifyplaylistwidget.network.SpotifyApi;

import javax.inject.Inject;

/**
 * Activity without an UI whose only purpose is to fetch a Spotify access token and give it to
 * SpotifyApi.
 */
public class AuthActivity extends AppCompatActivity {

    private static final String TAG = "AuthActivity";
    private static final String CLIENT_ID = "a2277433f3ba4c9a9b0ae0859c30f808";
    private static final String REDIRECT_URI = "app://logincallback";
    private static final int REQUEST_CODE = 1337;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Inject
    SpotifyApi mSpotifyApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        IoC.getInjector().inject(this);
        super.onCreate(savedInstanceState);

        //This activity doesnt need any UI?
        //setContentView(R.layout.activity_auth);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Log.d(TAG, "on create");
        if (!spotifyInstalled()) {
            mFirebaseAnalytics.logEvent("spotify_not_installed", new Bundle());
            AuthorizationClient.openDownloadSpotifyActivity(this);
            quitWithMessage(getString(R.string.spotify_install_ap));
        } else {
            openAuthenticationActivity();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void openAuthenticationActivity() {

        AuthorizationRequest request = new AuthorizationRequest.Builder(CLIENT_ID, AuthorizationResponse.Type.TOKEN, REDIRECT_URI)
                .setScopes(new String[]{"user-read-private",
                        "playlist-read-private",
                        "playlist-read-collaborative"})
                .build();

        AuthorizationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    private void authenticationFailed(String reason) {
        quitWithMessage(getString(R.string.spotify_auth_error) + " (" + reason + ")");
    }

    private void authenticationCancelled() {
        quitWithMessage(getString(R.string.spotify_auth_required));
    }

    private boolean spotifyInstalled() {
        try {
            this.getPackageManager().getPackageInfo("com.spotify.music", 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private void quitWithMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();
        crashlytics.log("[AuthActivity.onActivityResult] requestCode: " + requestCode);
        crashlytics.log("[AuthActivity.onActivityResult] resultCode: " + resultCode);
        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, intent);
            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    crashlytics.log("Auth success");
                    // Get token and go to select view
                    String token = response.getAccessToken();
                    int expiresInSeconds = response.getExpiresIn();

                    mSpotifyApi.setAccessToken(token, expiresInSeconds);

                    mFirebaseAnalytics.logEvent("auth_success", new Bundle());
                    setResult(RESULT_OK);
                    finish();
                    overridePendingTransition(R.anim.fade_in_hard_nodelay, R.anim.hide_delayed);
                    break;

                // Auth flow returned an error
                case ERROR:
                    crashlytics.log("Auth response type: " + response.getType());
                    crashlytics.log("Auth response code: " + response.getCode());
                    crashlytics.log("Auth response state: " + response.getState());
                    crashlytics.log("Auth response error: " + response.getError());
                    crashlytics.recordException(new AuthErrorException());

                    mFirebaseAnalytics.logEvent("auth_error", new Bundle());
                    Log.d("auth result", response.getError());

                    authenticationFailed(response.getError());
                    break;
                // Most likely auth flow was cancelled
                default:
                    crashlytics.log("Auth response type: " + response.getType());
                    crashlytics.log("Auth response code: " + response.getCode());
                    crashlytics.log("Auth response state: " + response.getState());
                    crashlytics.log("Auth response error: " + response.getError());
                    crashlytics.recordException(new AuthException());

                    mFirebaseAnalytics.logEvent("auth_cancel", new Bundle());
                    Log.d("auth result", "cancelled");

                    authenticationCancelled();
            }
        }
    }
}