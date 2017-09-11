package com.wavy.spotifyplaylistwidget.DI;

import com.wavy.spotifyplaylistwidget.ActivityTestBase;
import com.wavy.spotifyplaylistwidget.AuthActivity;
import com.wavy.spotifyplaylistwidget.PlaylistWidgetConfigureActivityBase;
import com.wavy.spotifyplaylistwidget.SelectActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules={TestAppModule.class})
public interface TestAppComponent extends AppComponent {
    void inject(ActivityTestBase activityTestBase);
    void inject(SelectActivity activity);
    void inject(AuthActivity activity);
    void inject(PlaylistWidgetConfigureActivityBase activity);
}
