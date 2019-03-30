package com.wavy.spotifyplaylistwidget.db.entity;

import com.wavy.spotifyplaylistwidget.db.converter.BooleanConverter;

import androidx.room.TypeConverters;

public class WidgetOptions {

    public String backgroundColor;
    public int backgroundOpacity;
    public String primaryTextColor;
    public String secondaryTextColor;
    @TypeConverters(BooleanConverter.class)
    public boolean showEditButton;
    @TypeConverters(BooleanConverter.class)
    public boolean showTrackCount;

    public WidgetOptions(String backgroundColor, int backgroundOpacity, String primaryTextColor, String secondaryTextColor,  Boolean showEditButton, Boolean showTrackCount) {
        this.backgroundColor = backgroundColor;
        this.backgroundOpacity = backgroundOpacity;
        this.primaryTextColor = primaryTextColor;
        this.secondaryTextColor = secondaryTextColor;
        this.showEditButton = showEditButton;
        this.showTrackCount = showTrackCount;
    }

    public static WidgetOptions getDefaultOptions() {
        return new WidgetOptions("#121314", 100, "#ffffff", "#A0A0A0", true, true);
    }
}
