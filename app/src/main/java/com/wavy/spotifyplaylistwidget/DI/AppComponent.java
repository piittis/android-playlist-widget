package com.wavy.spotifyplaylistwidget.DI;

import com.wavy.spotifyplaylistwidget.AuthActivity;
import com.wavy.spotifyplaylistwidget.PlaylistWidgetConfigureActivityBase;
import com.wavy.spotifyplaylistwidget.SelectActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules={AppModule.class})
public interface AppComponent extends AppInjector{

}
