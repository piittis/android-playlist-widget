package com.wavy.spotifyplaylistwidget.db.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

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