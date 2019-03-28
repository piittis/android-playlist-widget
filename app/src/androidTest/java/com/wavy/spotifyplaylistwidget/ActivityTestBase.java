package com.wavy.spotifyplaylistwidget;


import com.wavy.spotifyplaylistwidget.DI.AppInjector;
import com.wavy.spotifyplaylistwidget.DI.DaggerTestAppComponent;
import com.wavy.spotifyplaylistwidget.DI.TestAppComponent;
import com.wavy.spotifyplaylistwidget.DI.TestAppModule;
import com.wavy.spotifyplaylistwidget.db.AppDatabase;
import com.wavy.spotifyplaylistwidget.db.entity.PlaylistEntity;
import com.wavy.spotifyplaylistwidget.db.entity.WidgetEntity;
import com.wavy.spotifyplaylistwidget.db.entity.WidgetOptions;
import com.wavy.spotifyplaylistwidget.db.entity.WidgetPlaylist;
import com.wavy.spotifyplaylistwidget.network.SpotifyApi;
import com.wavy.spotifyplaylistwidget.viewModels.PlaylistViewModel;

import org.threeten.bp.Instant;

import java.util.ArrayList;

import javax.inject.Inject;

/**
 * This should set up all mocking and any boilerplate needed for testing.
 */
public class ActivityTestBase {

    @Inject
    protected SpotifyApi mockSpotifyApi;

    @Inject
    protected PlaylistsContainer mockPlaylistContainer;

    @Inject
    protected AppDatabase mockDatabase;

    public void initialize() {

        AppInjector testAppComponent = DaggerTestAppComponent.builder()
                .testAppModule(new TestAppModule())
                .build();

        ((TestAppComponent)testAppComponent).inject(this);

        // Set component so activities under test receive same mocks
        IoC.setTestInjector(testAppComponent);

        clearDb();
    }

    protected ArrayList<PlaylistViewModel> getTestPlaylists(int count) {
        ArrayList<PlaylistViewModel> models = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            models.add(new PlaylistViewModel("Playlist"+i, "id"+i, "uri"+i, null, i, "user"+1));
        }
        return models;
    }

    /**
     * Inserts a mock widget that contains the given playlists
     */
    protected void insertMockWidget(ArrayList<PlaylistViewModel> testPlaylists) {
        mockDatabase.widgetDao().upsert(new WidgetEntity(1, Instant.now(), WidgetOptions.getDefaultOptions()));
        ArrayList<PlaylistEntity> playlists = new ArrayList<>();
        ArrayList<WidgetPlaylist> widgetplaylists = new ArrayList<>();
        for (int i = 0; i < testPlaylists.size(); i++) {
            PlaylistViewModel pl = testPlaylists.get(i);
            playlists.add(new PlaylistEntity(pl.id, pl.name, pl.uri, pl.owner, pl.tracks));
            widgetplaylists.add(new WidgetPlaylist(1, pl.id, i+1));
        }
        mockDatabase.playlistDao().upsertAll(playlists);
        mockDatabase.widgetPlaylistDao().setWidgetsPlaylists(1, widgetplaylists);
    }

    private void clearDb() {
        mockDatabase.query("delete from widget_playlists", null);
        mockDatabase.query("delete from widgets", null);
        mockDatabase.query("delete from playlists", null);
    }

}