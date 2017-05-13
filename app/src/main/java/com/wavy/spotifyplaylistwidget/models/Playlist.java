package com.wavy.spotifyplaylistwidget.models;

public class Playlist {

    public String name;
    public String id;
    public int tracks;
    public String owner;
    public Boolean selected = false;

    public Playlist(String name, String id, int tracks, String owner) {
        this.name = name;
        this.id = id;
        this.tracks = tracks;
        this.owner = owner;
    }
}
