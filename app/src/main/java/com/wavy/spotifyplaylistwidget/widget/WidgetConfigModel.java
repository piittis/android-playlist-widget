package com.wavy.spotifyplaylistwidget.widget;

import java.util.ArrayList;
import java.util.Date;

public class WidgetConfigModel {

    public static final int TYPE_MULTI = 1;
    public static final int TYPE_SINGLE = 1;

    private int widgetType;
    private Boolean showImages = true;
    private Date creationDate;

    private ArrayList<PlaylistModel> playlists;

    public WidgetConfigModel(int widgetType) {
        this.creationDate = new Date(System.currentTimeMillis());
        this.widgetType = widgetType;
        this.playlists = new ArrayList<>();
    }

    public void setPlaylists(ArrayList<PlaylistModel> playlists) {
        this.playlists = playlists;
    }

    public ArrayList<PlaylistModel> getPlaylists() {
        return playlists;
    }
}
