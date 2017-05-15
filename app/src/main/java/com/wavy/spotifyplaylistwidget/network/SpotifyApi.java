package com.wavy.spotifyplaylistwidget.network;

import com.wavy.spotifyplaylistwidget.models.Playlist;

import java.util.ArrayList;

public class SpotifyApi {

    public interface playlistsLoadedCallbackListener {
        void onPlaylistsLoaded(ArrayList<Playlist> playlists);
    }

    public void getPlaylists(playlistsLoadedCallbackListener callbackListener) {
        // Generate some random stuff for testing.
        ArrayList<Playlist> playlists = new ArrayList<>();

        for (int i = 0; i < 50; i++) {
            playlists.add(new Playlist("playlist"+i, Integer.toString(i), 1000, "http://soundplate.com/wp-content/uploads/LATE.png", "owner"+1));
        }

        callbackListener.onPlaylistsLoaded(playlists);
    }
}
