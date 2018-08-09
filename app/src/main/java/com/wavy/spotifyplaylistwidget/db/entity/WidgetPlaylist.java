package com.wavy.spotifyplaylistwidget.db.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.Relation;
import android.support.annotation.NonNull;

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
