package com.wavy.spotifyplaylistwidget;

import android.app.Application;
import com.jakewharton.threetenabp.AndroidThreeTen;

public class PlaylistWidgetApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //Stetho.initializeWithDefaults(this);

        AndroidThreeTen.init(this);
        IoC.Initialize(this);
    }

}
