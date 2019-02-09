package com.wavy.spotifyplaylistwidget.db;

import android.content.Context;

import com.wavy.spotifyplaylistwidget.db.dao.PlaylistDao;
import com.wavy.spotifyplaylistwidget.db.dao.WidgetDao;
import com.wavy.spotifyplaylistwidget.db.dao.WidgetPlaylistDao;
import com.wavy.spotifyplaylistwidget.db.entity.PlaylistEntity;
import com.wavy.spotifyplaylistwidget.db.entity.WidgetEntity;
import com.wavy.spotifyplaylistwidget.db.entity.WidgetPlaylist;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

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
                //.addMigrations(MIGRATION_1_2)
                .build();

        db.query("PRAGMA recursive_triggers = false", null);
        return db;
    }

    // For tests only.
    public static synchronized AppDatabase getInMemoryDatabse(Context context) {

        AppDatabase db = Room.inMemoryDatabaseBuilder(context.getApplicationContext(),
                AppDatabase.class)
                //.addMigrations(MIGRATION_1_2)
                .build();

        db.query("PRAGMA recursive_triggers = false", null);
        return db;
    }

    // This is how you do migrations
    /*static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE `Fruit` (`id` INTEGER, "
                    + "`name` TEXT, PRIMARY KEY(`id`))");
        }
    }*/

}
