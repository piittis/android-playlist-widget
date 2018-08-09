package com.wavy.spotifyplaylistwidget;

import android.content.Context;

import com.wavy.spotifyplaylistwidget.DI.AppInjector;
import com.wavy.spotifyplaylistwidget.DI.AppModule;
import com.wavy.spotifyplaylistwidget.DI.DaggerAppComponent;

/**
 * Tutorials mostly put these in an class extending Application.
 * I think its more nice to call IoC.getInjector than ((PlaylistWidgetApplication) getApplication).getInjector()
 */
public class IoC {

    private static AppInjector injector;

    // This must be called by the app before any activities are launched!
    public static void Initialize(Context applicationContext) {
        injector = DaggerAppComponent.builder()
                .appModule(new AppModule(applicationContext))
                .build();
    }

    public static AppInjector getInjector() {
        return injector;
    }

    public static void setTestInjector(AppInjector testInjector) {
        injector = testInjector;
    }
}