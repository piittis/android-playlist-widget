package com.wavy.spotifyplaylistwidget.DI;

import com.wavy.spotifyplaylistwidget.PlaylistsContainer;
import com.wavy.spotifyplaylistwidget.network.SpotifyApi;


import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static org.mockito.Mockito.mock;

@Module
public class TestAppModule {

    @Provides
    @Singleton
    SpotifyApi provideSpotifyApi() {
        return mock(SpotifyApi.class);
    }

    @Provides
    @Singleton
    PlaylistsContainer providePlaylistsContainer() {
        return new PlaylistsContainer();
    }
}
