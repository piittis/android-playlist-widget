package com.wavy.spotifyplaylistwidget.viewModels;

/**
 * Viewmodel for playlist that is used in the configure activities.
 */
public class PlaylistViewModel {

    public String name;
    public String id;
    public String uri;
    public String imageUrl;
    public int tracks;
    public String owner;
    public Boolean selected = false;

    public PlaylistViewModel() { }

    public PlaylistViewModel(String name, String id, String uri, String imageUrl, int tracks, String owner) {
        this.name = name;
        this.id = id;
        this.uri = uri;
        this.imageUrl = imageUrl;
        this.tracks = tracks;
        this.owner = owner;
    }
}
