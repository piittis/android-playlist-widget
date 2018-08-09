package com.wavy.spotifyplaylistwidget.DI;

import com.wavy.spotifyplaylistwidget.ArrangeActivity;
import com.wavy.spotifyplaylistwidget.AuthActivity;
import com.wavy.spotifyplaylistwidget.PlaylistWidgetConfigureActivityBase;
import com.wavy.spotifyplaylistwidget.SelectActivity;
import com.wavy.spotifyplaylistwidget.widget.PlaylistViewsFactory;
import com.wavy.spotifyplaylistwidget.widget.PlaylistWidgetProvider;

public interface AppInjector {
    void inject(SelectActivity activity);
    void inject(AuthActivity activity);
    void inject(ArrangeActivity activity);
    void inject(PlaylistWidgetConfigureActivityBase activity);
    void inject(PlaylistViewsFactory factory);
    void inject(PlaylistWidgetProvider provider);

}
