package com.wavy.spotifyplaylistwidget.DI;

import android.content.Context;

import com.wavy.spotifyplaylistwidget.PlaylistsContainer;
import com.wavy.spotifyplaylistwidget.db.AppDatabase;
import com.wavy.spotifyplaylistwidget.network.SpotifyApi;
import com.wavy.spotifyplaylistwidget.utils.FileHelper;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    private Context mApplicationContext;

    public AppModule(Context applicationContext) {
        mApplicationContext = applicationContext;
    }

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

    @Singleton
    @Provides
    AppDatabase provideAppDatabase() {
        return AppDatabase.getFileDatabase(mApplicationContext);
    }

    @Provides
    FileHelper provideFileHelper() { return new FileHelper(); }
}
