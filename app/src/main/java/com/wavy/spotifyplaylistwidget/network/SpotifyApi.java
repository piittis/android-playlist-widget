package com.wavy.spotifyplaylistwidget.network;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wavy.spotifyplaylistwidget.viewModels.PlaylistViewModel;

import org.joda.time.DateTime;

import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SpotifyApi {

    private static final String TAG = "SpotifyApi";
    private static String mAuthHeaderValue;
    private static String mAccessToken;
    private static DateTime mAccessTokenExpiresIn;
    private static final String mApiRoot = "https://api.spotify.com/v1/";

    private PlaylistService mPlaylistService;
    private WeakReference<spotifyApiErrorListener> mErrorListener;

    public interface playlistsLoadedCallbackListener {
        void onPlaylistsLoaded(int offset, ArrayList<PlaylistViewModel> playlists);
    }

    public interface spotifyApiErrorListener {
        void onSpotifyApiError(String reason);
    }

    public SpotifyApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mApiRoot)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mPlaylistService = retrofit.create(PlaylistService.class);
    }

    public void setAccessToken(String token, int expiresInSeconds) {
        mAccessToken = token;
        mAccessTokenExpiresIn = DateTime.now().plusSeconds(expiresInSeconds);
        mAuthHeaderValue = "Bearer " + token;
    }

    /**
     * Check if we have access token that is not expiring soon.
     */
    public Boolean isAccessTokenValid() {
        return mAccessToken != null && DateTime.now().plusMinutes(5).isBefore(mAccessTokenExpiresIn);
    }

    public void setErrorListener(spotifyApiErrorListener listener) {
        mErrorListener = new WeakReference<>(listener);
    }

    /**
     * Load playlists in batches of 50. Callback will be called for each batch.
     * @param offset The index of the first playlist to return.
     * @param callbackListener Callback is called multiple times if user has more than 50 playlists.
     */
    public void getPlaylists(int offset, playlistsLoadedCallbackListener callbackListener) {

        Call<PlaylistService.PlaylistResponseModel> call = mPlaylistService.getPlaylistsOfUser(mAuthHeaderValue, offset);

        call.enqueue(new Callback<PlaylistService.PlaylistResponseModel>() {
            @Override
            public void onResponse(@NonNull Call<PlaylistService.PlaylistResponseModel> call,
                                   @NonNull Response<PlaylistService.PlaylistResponseModel> response) {

               if (response.isSuccessful()) {
                   callbackListener.onPlaylistsLoaded(offset, getPlaylistViewModels(response.body()));

                   int playlistsLoaded = offset + 50;
                   if (playlistsLoaded < response.body().total) {
                       // More playlists to be had, recursively get more.
                       getPlaylists(offset + 50, callbackListener);
                   }
               } else {
                   callbackListener.onPlaylistsLoaded(offset, new ArrayList<>());
                   reportError(response.message());
               }
            }

            @Override
            public void onFailure(Call<PlaylistService.PlaylistResponseModel> call, Throwable t) {
                callbackListener.onPlaylistsLoaded(offset, new ArrayList<>());
                reportError(t.getMessage());
            }
        });
    }

    private static ArrayList<PlaylistViewModel> getPlaylistViewModels(PlaylistService.PlaylistResponseModel response) {

        ArrayList<PlaylistViewModel> list = new ArrayList<>(response.playlists.size());
        for (PlaylistService.PlaylistModel pl : response.playlists) {
            if (pl.name != null && pl.id != null) {
                list.add(getPlaylistViewModel(pl));
            }
        }
        return list;
    }

    private static PlaylistViewModel getPlaylistViewModel(PlaylistService.PlaylistModel response) {
        PlaylistViewModel vm = new PlaylistViewModel();
        vm.name = response.name;
        vm.id = response.id;
        vm.uri = response.uri;
        vm.imageUrl = getImageUrl(response.images);
        vm.tracks = (response.tracks != null) ? response.tracks.total : 0;
        vm.owner = (response.owner != null) ? response.owner.id : "";

        return vm;
    }

    // Select the image the app should use
    private static String getImageUrl(List<PlaylistService.ImageModel> images) {
        if (images == null || images.size() == 0) return null;

        // Get first image with at most 300px width.
        PlaylistService.ImageModel imageToUse = images.get(0);
        for(PlaylistService.ImageModel i : images) {
            if (i.width <= 300) {
                imageToUse = i;
                break;
            }
        }
        return imageToUse.url;
    }

    private void reportError(String reason) {
        if (mErrorListener.get() != null) {
            mErrorListener.get().onSpotifyApiError(reason);
        }
    }

}
