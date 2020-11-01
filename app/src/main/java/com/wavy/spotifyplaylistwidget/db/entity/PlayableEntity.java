package com.wavy.spotifyplaylistwidget.db.entity;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.wavy.spotifyplaylistwidget.db.converter.PlayableTypeConverter;

@Entity(tableName = "playables")
public class PlayableEntity {

    // Unique id from spotify.
    // When a playlist is inserted, it will replace existing row if conflict occurs.
    @PrimaryKey
    @NonNull
    public String spotifyId;

    public String name;

    public String uri;

    public String owner;

    public int trackCount;

    @NonNull
    @TypeConverters(PlayableTypeConverter.class)
    public PlayableType type;

    public PlayableEntity(String spotifyId, String name, String uri, String owner, int trackCount, PlayableType type) {
        this.spotifyId = spotifyId;
        this.name = name;
        this.uri = uri;
        this.owner = owner;
        this.trackCount = trackCount;
        this.type = type;
    }
}