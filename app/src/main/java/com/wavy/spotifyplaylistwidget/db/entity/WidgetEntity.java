package com.wavy.spotifyplaylistwidget.db.entity;

import com.wavy.spotifyplaylistwidget.db.converter.InstantConverter;

import org.threeten.bp.Instant;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

@Entity(tableName = "widgets",
        indices = {@Index(value = "androidWidgetId", name = "index_widgets_androidWidgetId")})
public class WidgetEntity {

    @PrimaryKey
    // This is the id that android gives to widgets
    public int androidWidgetId;

    @TypeConverters(InstantConverter.class)
    public Instant creationTime;

    @Embedded
    @NonNull
    public WidgetOptions options;

    public WidgetEntity(int androidWidgetId, Instant creationTime, WidgetOptions options) {
        this.androidWidgetId = androidWidgetId;
        this.creationTime = creationTime;
        this.options = options;
    }

}