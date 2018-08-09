package com.wavy.spotifyplaylistwidget.db.entity;

public class WidgetOptions {

    public String backgroundColor;
    public String primaryTextColor;
    public String secondaryTextColor;

    public WidgetOptions(String backgroundColor, String primaryTextColor, String secondaryTextColor) {

        this.backgroundColor = backgroundColor;
        this.primaryTextColor = primaryTextColor;
        this.secondaryTextColor = secondaryTextColor;
    }

    public static WidgetOptions getDefaultOptions() {
        return new WidgetOptions("#121314", "#ffffff", "#A0A0A0");
    }
}
