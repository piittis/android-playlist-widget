package com.wavy.spotifyplaylistwidget;


import com.wavy.spotifyplaylistwidget.DI.AppInjector;
import com.wavy.spotifyplaylistwidget.DI.DaggerTestAppComponent;
import com.wavy.spotifyplaylistwidget.DI.TestAppComponent;
import com.wavy.spotifyplaylistwidget.DI.TestAppModule;
import com.wavy.spotifyplaylistwidget.network.SpotifyApi;

import javax.inject.Inject;

/**
 * This should set up all mocking and any boiler plate needed for testing.
 */
public class ActivityTestBase {

    @Inject
    protected SpotifyApi mockSpotifyApi;

    @Inject
    protected PlaylistsContainer playlistsContainer;

    public void initialize() {

        AppInjector testAppComponent = DaggerTestAppComponent.builder()
                .testAppModule(new TestAppModule())
                .build();

        ((TestAppComponent)testAppComponent).inject(this);

        // Set component so activities under test receive same mocks
        IoC.setTestComponent(testAppComponent);
    }
}