package com.wavy.spotifyplaylistwidget.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.wavy.spotifyplaylistwidget.db.dao.PlayableDao;
import com.wavy.spotifyplaylistwidget.db.dao.WidgetDao;
import com.wavy.spotifyplaylistwidget.db.dao.WidgetPlayablesDao;
import com.wavy.spotifyplaylistwidget.db.entity.PlayableEntity;
import com.wavy.spotifyplaylistwidget.db.entity.WidgetEntity;
import com.wavy.spotifyplaylistwidget.db.entity.WidgetPlayables;

@Database(entities = {WidgetEntity.class, PlayableEntity.class, WidgetPlayables.class}, version = 3)

public abstract class AppDatabase extends RoomDatabase {


    public abstract WidgetDao widgetDao();
    public abstract PlayableDao playlistDao();
    public abstract WidgetPlayablesDao widgetPlayablesDao();

    public static synchronized AppDatabase getFileDatabase(Context context) {

        AppDatabase db = Room.databaseBuilder(context.getApplicationContext(),
                AppDatabase.class,
                "spotify-playlist-widget-db")
                // Widget must do synchronous main thread queries.
                // Otherwise we do such light queries that using main thread is fine.
                .allowMainThreadQueries()
                .addMigrations(MIGRATION_1_2)
                .addMigrations(MIGRATION_2_3)
                .build();

        db.query("PRAGMA recursive_triggers = false", null);
        return db;
    }

    // For tests only.
    public static synchronized AppDatabase getInMemoryDatabse(Context context) {

        AppDatabase db = Room.inMemoryDatabaseBuilder(context.getApplicationContext(),
                AppDatabase.class)
                .allowMainThreadQueries()
                .addMigrations(MIGRATION_1_2)
                .build();

        db.query("PRAGMA recursive_triggers = false", null);
        return db;
    }

    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE widgets ADD COLUMN backgroundOpacity INTEGER NOT NULL DEFAULT 100");
            database.execSQL("ALTER TABLE widgets ADD COLUMN showEditButton INTEGER NOT NULL DEFAULT 1");
            database.execSQL("ALTER TABLE widgets ADD COLUMN showTrackCount INTEGER NOT NULL DEFAULT 1");
        }
    };

    public static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE playlists RENAME TO playables");
            database.execSQL("ALTER TABLE playables ADD COLUMN type INTEGER NOT NULL DEFAULT 1 CHECK(type = 1 OR type = 2 OR type = 3)");

            database.execSQL("CREATE TABLE IF NOT EXISTS widget_playables (" +
                    "widgetId INTEGER NOT NULL," +
                    "playableId TEXT NOT NULL," +
                    "playablePosition INTEGER NOT NULL," +
                    "PRIMARY KEY(widgetId, playableId)," +
                    "FOREIGN KEY(widgetId) REFERENCES widgets (androidWidgetId) " +
                    "ON UPDATE no action " +
                    "ON DELETE CASCADE," +
                    "FOREIGN KEY(playableId) REFERENCES playables (spotifyId) " +
                    "ON UPDATE no action " +
                    "ON DELETE no action" +
                    ")");

            database.execSQL("INSERT INTO widget_playables (widgetId, playableId, playablePosition) select widgetId, playlistId, playlistPosition from widget_playlists");
            database.execSQL("DROP TABLE widget_playlists");
        }
    };

}
