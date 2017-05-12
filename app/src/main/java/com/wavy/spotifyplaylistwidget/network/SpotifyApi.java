package com.wavy.spotifyplaylistwidget.network;

import com.wavy.spotifyplaylistwidget.models.Playlist;

import java.util.ArrayList;

public class SpotifyApi {


    public ArrayList<Playlist> getPlaylists() {
        // Generate some random stuff for testing.
        ArrayList<Playlist> playlists = new ArrayList<>();

        for (int i = 0; i < 50; i++) {
            playlists.add(new Playlist("playlist"+i, Integer.toString(i), 1000, "owner"+1));
        }

        return playlists;
    }
}
