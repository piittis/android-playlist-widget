package com.wavy.spotifyplaylistwidget.db.entity;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.wavy.spotifyplaylistwidget.db.converter.InstantConverter;

import org.threeten.bp.Instant;

@Entity(tableName = "widgets",
        indices = {@Index(value = "androidWidgetId", name = "index_widgets_androidWidgetId")})

public class WidgetEntity {

    @PrimaryKey
    // This is the id that android gives to widgets
    public int androidWidgetId;

    @TypeConverters(InstantConverter.class)
    public Instant creationTime;

    @Embedded
    public WidgetOptions options;

    public WidgetEntity(int androidWidgetId, Instant creationTime, WidgetOptions options) {
        this.androidWidgetId = androidWidgetId;
        this.creationTime = creationTime;
        this.options = options;
    }

}