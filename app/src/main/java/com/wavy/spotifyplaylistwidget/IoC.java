package com.wavy.spotifyplaylistwidget;

import com.wavy.spotifyplaylistwidget.DI.AppInjector;
import com.wavy.spotifyplaylistwidget.DI.AppModule;
import com.wavy.spotifyplaylistwidget.DI.DaggerAppComponent;

/**
 * Tutorials mostly put these in an class extending Application.
 * I think this is just as good and more easily accessed.
 */
public class IoC {

    private static AppInjector injector;

    static {
        injector = DaggerAppComponent.builder()
                .appModule(new AppModule())
                .build();
    }

    public static AppInjector getComponent() {
        return injector;
    }

    public static void setTestCompoinent(AppInjector testComponent) {
        injector = testComponent;
    }

}
