package com.wavy.spotifyplaylistwidget.DI;

import com.wavy.spotifyplaylistwidget.AuthActivity;
import com.wavy.spotifyplaylistwidget.PlaylistWidgetConfigureActivityBase;
import com.wavy.spotifyplaylistwidget.SelectActivity;

public interface AppInjector {
    void inject(SelectActivity activity);
    void inject(AuthActivity activity);
    void inject(PlaylistWidgetConfigureActivityBase activity);
}
