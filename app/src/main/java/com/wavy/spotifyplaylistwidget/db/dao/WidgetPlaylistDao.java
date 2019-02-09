package com.wavy.spotifyplaylistwidget.db.dao;


import com.wavy.spotifyplaylistwidget.db.entity.PlaylistEntity;
import com.wavy.spotifyplaylistwidget.db.entity.WidgetPlaylist;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

@Dao
public abstract class WidgetPlaylistDao {

    @Query("delete from widget_playlists where widgetId = :widgetId")
    abstract void deleteByWidgetId(int widgetId);

    @Insert
    abstract void insertAll(List<WidgetPlaylist> playlists);

    @Transaction
    public void setWidgetsPlaylists(int widgetId, List<WidgetPlaylist> playlists) {
        deleteByWidgetId(widgetId);
        insertAll(playlists);
    }

    @Query("select pl.* from widget_playlists wp " +
            "join playlists pl on (pl.spotifyId = wp.playlistId) " +
            "where wp.widgetId = :androidWidgetId " +
            "order by wp.playlistPosition asc")
    public abstract List<PlaylistEntity> getWidgetPlaylists(int androidWidgetId);
}
