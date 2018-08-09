package com.wavy.spotifyplaylistwidget.widget;

// TODO: remove when all users are on sqlite
public class PlaylistModel {

    public String id;
    public String name;
    public String uri;
    public String owner;
    public String imagePath;
    public int tracks;

    public PlaylistModel(String name, String id, String uri, int tracks, String imagePath, String owner) {
        this.name = name;
        this.id = id;
        this.uri = uri;
        this.tracks = tracks;
        this.imagePath = imagePath;
        this.owner = owner;
    }
}


