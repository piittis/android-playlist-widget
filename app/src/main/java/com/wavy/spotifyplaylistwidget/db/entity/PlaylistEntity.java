package com.wavy.spotifyplaylistwidget.db.entity;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "playlists")
public class PlaylistEntity {

    // Unique id from spotify.
    // When a playlist is inserted, it will replace existing row if conflict occurs.
    @PrimaryKey
    @NonNull
    public String spotifyId;

    public String name;

    public String uri;

    public String owner;

    public int trackCount;

    public PlaylistEntity(String spotifyId, String name, String uri, String owner, int trackCount) {
        this.spotifyId = spotifyId;
        this.name = name;
        this.uri = uri;
        this.owner = owner;
        this.trackCount = trackCount;
    }
}