package com.wavy.spotifyplaylistwidget.DI;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules={AppModule.class})
public interface AppComponent extends AppInjector{

}
