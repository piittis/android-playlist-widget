package com.wavy.spotifyplaylistwidget.network;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wavy.spotifyplaylistwidget.viewModels.PlaylistViewModel;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SpotifyApi {


    private static final String TAG = "SpotifyApi";
    private static String mAuthHeaderValue;
    private static final String mApiRoot = "https://api.spotify.com/v1/";

    private PlaylistService mPlaylistService;
    private spotifyApiErrorListener mErrorListener;

    public interface playlistsLoadedCallbackListener {
        void onPlaylistsLoaded(int offset, ArrayList<PlaylistViewModel> playlists);
    }

    public interface spotifyApiErrorListener {
        void onSpotifyApiError(String reason);
    }

    // Singleton
    private static SpotifyApi instance;

    public static SpotifyApi getInstance() {
        if (instance == null) {
            instance = new SpotifyApi();
        }
        return instance;
    }

    // Allow setting a mock instance from tests
    public static void setInstance(SpotifyApi newInstance) {
        instance = newInstance;
    }

    private SpotifyApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mApiRoot)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mPlaylistService = retrofit.create(PlaylistService.class);
    }

    public void setAccessToken(String token) {
        mAuthHeaderValue = "Bearer " + token;
    }

    public Boolean isAccessTokenSet() {
        return mAuthHeaderValue != null;
    }

    public void setErrorListener(spotifyApiErrorListener listener) {
        mErrorListener = listener;
    }

    /**
     * Load playlists in batches of 50. Callback will be called for each batch.
     * @param offset The index of the first playlist to return.
     * @param callbackListener Listener that will be called with results.
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
            PlaylistViewModel vm = new PlaylistViewModel(pl.name, pl.id, pl.uri, getImageUrl(pl.images),
                    pl.tracks.total, pl.owner.id);
            list.add(vm);
        }
        return list;
    }

    // Select the image the app should use
    private static String getImageUrl(List<PlaylistService.ImageModel> images) {
        if (images.size() == 0) return null;

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
        if (mErrorListener != null) {
            mErrorListener.onSpotifyApiError(reason);
        }
    }
}
