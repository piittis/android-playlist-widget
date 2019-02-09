package com.wavy.spotifyplaylistwidget.network;


import com.wavy.spotifyplaylistwidget.viewModels.PlaylistViewModel;

import org.threeten.bp.Instant;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SpotifyApi {

    private static final String TAG = "SpotifyApi";
    private static String mAuthHeaderValue;
    private static String mAccessToken;

    private static Instant mAccessTokenExpiresIn;
    private static final String mApiRoot = "https://api.spotify.com/v1/";

    private PlaylistService mPlaylistService;

    public SpotifyApi() {

        // Inly use custom client for debugging! stick with default for release.
       /* OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();*/

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mApiRoot)
                //.client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mPlaylistService = retrofit.create(PlaylistService.class);
    }

    public void setAccessToken(String token, int expiresInSeconds) {
        mAccessToken = token;
        mAccessTokenExpiresIn = Instant.now().plusSeconds(expiresInSeconds);
        mAuthHeaderValue = "Bearer " + token;
    }

    /**
     * Check if we have access token that is not expiring soon.
     */
    public Boolean isAccessTokenValid() {
        return mAccessToken != null && Instant.now().plusSeconds(5 * 60).isBefore(mAccessTokenExpiresIn);
    }

    /**
     * Get an observable that will emit all user playlists 50 at a time.
     */
    public Observable<ArrayList<PlaylistViewModel>> getPlaylists() {
        return Observable.create(emitter -> emitPlaylists(0, emitter));
    }

    private void emitPlaylists(int offset, ObservableEmitter<ArrayList<PlaylistViewModel>> emitter) {

        // Load some playlists.
        Call<PlaylistService.PlaylistResponseModel> call = mPlaylistService.getPlaylistsOfUser(mAuthHeaderValue, offset);
        call.enqueue(new Callback<PlaylistService.PlaylistResponseModel>() {
            @Override
            public void onResponse(@NonNull Call<PlaylistService.PlaylistResponseModel> call,
                                   @NonNull Response<PlaylistService.PlaylistResponseModel> response) {
                if (emitter.isDisposed()) return;

                if (response.isSuccessful()) {

                    emitter.onNext(getPlaylistViewModels(response.body()));
                    int playlistsLoaded = offset + 50;
                    if (playlistsLoaded < response.body().total) {
                        // More playlists to be had, recursively emit more.
                        emitPlaylists(offset + 50, emitter);
                    } else {
                        emitter.onComplete();
                    }
                } else {
                    emitter.onNext(new ArrayList<>());
                    emitter.onComplete();
                    emitter.onError(new Error(response.message()));
                }
            }

            @Override
            public void onFailure(Call<PlaylistService.PlaylistResponseModel> call, Throwable t) {
                if (emitter.isDisposed()) return;

                emitter.onNext(new ArrayList<>());
                emitter.onComplete();
                emitter.onError(t);
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

    // Select the image the app should use,
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


}
