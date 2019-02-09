package com.wavy.spotifyplaylistwidget.db.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(tableName = "widget_playlists",
        primaryKeys = {"widgetId", "playlistId"},
        foreignKeys = {
                @ForeignKey(entity = WidgetEntity.class,
                            parentColumns = "androidWidgetId",
                            childColumns = "widgetId",
                            onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = PlaylistEntity.class,
                            parentColumns = "spotifyId",
                            childColumns = "playlistId")
                },
        indices = {@Index(value = "playlistId", name = "widget_playlists_playlistId")})

public class WidgetPlaylist {

    public int widgetId;
    @NonNull
    public String playlistId;
    public int playlistPosition;

    public WidgetPlaylist(int widgetId, @NonNull String playlistId, int playlistPosition) {
        this.widgetId = widgetId;
        this.playlistId = playlistId;
        this.playlistPosition = playlistPosition;
    }

}
