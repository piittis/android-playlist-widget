package com.wavy.spotifyplaylistwidget.network;

import android.support.annotation.NonNull;
import android.util.Log;

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

    public interface playlistsLoadedCallbackListener {
        void onPlaylistsLoaded(int offset, ArrayList<PlaylistViewModel> playlists);
    }

    public SpotifyApi() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mApiRoot)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mPlaylistService = retrofit.create(PlaylistService.class);
    }

    public static void setAccessToken(String token) {
        mAuthHeaderValue = "Bearer " + token;
    }

    public static Boolean isAccessTokenSet() {
        return mAuthHeaderValue != null;
    }

    /**
     * Load playlists in batches of 50. Callback will be called for each batch.
     * @param offset The index of the first playlist to return.
     * @param callbackListener Listener that will be called with results.
     */
    public void getPlaylists(int offset, playlistsLoadedCallbackListener callbackListener) {

        Call<PlaylistService.PlaylistResponseModel> call = mPlaylistService.getPlaylistsOfUser(mAuthHeaderValue, offset);
        Log.d(TAG, "getPlaylists, offset " + offset);

        call.enqueue(new Callback<PlaylistService.PlaylistResponseModel>() {
            @Override
            public void onResponse(@NonNull Call<PlaylistService.PlaylistResponseModel> call,
                                   @NonNull Response<PlaylistService.PlaylistResponseModel> response) {


                callbackListener.onPlaylistsLoaded(offset, getPlaylistViewModels(response.body()));
                int playlistsLoaded = offset + 50;
                if (playlistsLoaded < response.body().total) {
                    // Recursively get more playlists
                    getPlaylists(offset + 50, callbackListener);
                }
            }

            @Override
            public void onFailure(Call<PlaylistService.PlaylistResponseModel> call, Throwable t) {
                //todo handle somehow
            }
        });
    }

    private static ArrayList<PlaylistViewModel> getPlaylistViewModels(PlaylistService.PlaylistResponseModel response) {

        ArrayList<PlaylistViewModel> list = new ArrayList<>(response.playlists.size());
        for (PlaylistService.PlaylistModel pl : response.playlists) {
            PlaylistViewModel vm = new PlaylistViewModel(pl.name, pl.id, pl.tracks.total,
                    getSmallestImageUrl(pl.images));
            list.add(vm);
        }
        return list;
    }

    // use smallest image since they are only 50dp on screen
    private static String getSmallestImageUrl(List<PlaylistService.ImageModel> images) {
        if (images.size() == 0) return null;

        PlaylistService.ImageModel smallest = images.get(0);
        for(PlaylistService.ImageModel i : images) {
            if (i.width < smallest.width) {
                smallest = i;
            }
        }
        return smallest.url;
    }
}
