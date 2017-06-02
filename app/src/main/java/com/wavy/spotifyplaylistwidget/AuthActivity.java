package com.wavy.spotifyplaylistwidget;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.wavy.spotifyplaylistwidget.network.SpotifyApi;

/**
 * Activity without an UI whose only purpose is to fetch a Spotify access token and give it to
 * SpotifyApi.
 */
public class AuthActivity extends AppCompatActivity {

    private static final String TAG = "AuthActivity";
    private static final String CLIENT_ID = "a2277433f3ba4c9a9b0ae0859c30f808";
    private static final String REDIRECT_URI = "app://logincallback";
    private static final int REQUEST_CODE = 1337;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.getBoolean("authFlowStarted", false))
            return;

        // This activity doesnt need any UI?
        //setContentView(R.layout.activity_auth);
        Log.d(TAG, "on create");
        if (!spotifyInstalled()) {
            AuthenticationClient.openDownloadSpotifyActivity(this);
            quitWithMessage(getString(R.string.spotify_install_ap));
        } else {
            openAuthenticationActivity();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState");
        outState.putBoolean("authFlowStarted", true);
        /*outState.putStringArray("selectedPlaylistIds",
                mSelectedPlaylistIds.toArray(new String[mSelectedPlaylistIds.size()]));*/
    }

    private void openAuthenticationActivity() {

            AuthenticationRequest.Builder builder =
                    new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);

            builder.setScopes(new String[]{"user-read-private",
                    "playlist-read-private",
                    "playlist-read-collaborative"});

            AuthenticationRequest request = builder.build();
            AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    private void authenticationFailed() {
        setResult(RESULT_CANCELED);
        quitWithMessage(getString(R.string.spotify_auth_error));
    }

    private void authenticationCancelled() {
        setResult(RESULT_CANCELED);
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
        this.finishAffinity();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            Log.d("auth result", "token");
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    // Get token and go to select view
                    String token = response.getAccessToken();
                    SpotifyApi.setAccessToken(token);

                    setResult(RESULT_OK);
                    finish();
                    overridePendingTransition(R.anim.fade_in_hard_nodelay, R.anim.hide_delayed);
                    break;

                // Auth flow returned an error
                case ERROR:
                    Log.d("auth result", "error");
                    authenticationFailed();

                // Most likely auth flow was cancelled
                default:
                    Log.d("auth result", "cancelled");
                    authenticationCancelled();
                    // Handle other cases
            }
        }
    }
}