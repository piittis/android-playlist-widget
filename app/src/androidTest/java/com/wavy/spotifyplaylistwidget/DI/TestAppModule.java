package com.wavy.spotifyplaylistwidget.DI;


import com.wavy.spotifyplaylistwidget.PlaylistsContainer;
import com.wavy.spotifyplaylistwidget.db.AppDatabase;
import com.wavy.spotifyplaylistwidget.network.SpotifyApi;
import com.wavy.spotifyplaylistwidget.utils.FileHelper;

import javax.inject.Singleton;

import androidx.test.InstrumentationRegistry;
import dagger.Module;
import dagger.Provides;
import io.reactivex.Completable;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
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

    @Singleton
    @Provides
    AppDatabase provideAppDatabase() {
        return AppDatabase.getInMemoryDatabse(InstrumentationRegistry.getTargetContext());
    }

    @Provides
    FileHelper provideFileHelper() {
        FileHelper mock = mock(FileHelper.class);
        doReturn(Completable.complete()).when(mock).persistPlaylistImages(any(), any());
        return mock;
    }
}
