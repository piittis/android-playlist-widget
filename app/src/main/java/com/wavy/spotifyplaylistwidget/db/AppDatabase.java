package com.wavy.spotifyplaylistwidget.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.wavy.spotifyplaylistwidget.db.dao.PlaylistDao;
import com.wavy.spotifyplaylistwidget.db.dao.WidgetDao;
import com.wavy.spotifyplaylistwidget.db.dao.WidgetPlaylistDao;
import com.wavy.spotifyplaylistwidget.db.entity.PlaylistEntity;
import com.wavy.spotifyplaylistwidget.db.entity.WidgetEntity;
import com.wavy.spotifyplaylistwidget.db.entity.WidgetPlaylist;

@Database(entities = {WidgetEntity.class, PlaylistEntity.class, WidgetPlaylist.class}, version = 1)

public abstract class AppDatabase extends RoomDatabase {


    public abstract WidgetDao widgetDao();
    public abstract PlaylistDao playlistDao();
    public abstract WidgetPlaylistDao widgetPlaylistDao();

    public static synchronized AppDatabase getFileDatabase(Context context) {

        AppDatabase db = Room.databaseBuilder(context.getApplicationContext(),
                AppDatabase.class,
                "spotify-playlist-widget-db")
                // Widget must do synchronous main thread queries.
                .allowMainThreadQueries()
                .build();

        db.query("PRAGMA recursive_triggers = false", null);
        return db;
    }

    // For tests only.
    public static synchronized AppDatabase getInMemoryDatabse(Context context) {

        AppDatabase db = Room.inMemoryDatabaseBuilder(context.getApplicationContext(),
                AppDatabase.class)
                .build();

        db.query("PRAGMA recursive_triggers = false", null);
        return db;
    }

}
