package com.wavy.spotifyplaylistwidget.DI;

import com.wavy.spotifyplaylistwidget.PlaylistsContainer;
import com.wavy.spotifyplaylistwidget.network.SpotifyApi;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    @Singleton
    @Provides
    SpotifyApi provideSpotifyApi() {
        return new SpotifyApi();
    }

    @Singleton
    @Provides
    PlaylistsContainer providePlaylistsContainer() {
        return new PlaylistsContainer();
    }
}
