package com.wavy.spotifyplaylistwidget;

import com.wavy.spotifyplaylistwidget.viewModels.PlaylistViewModel;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Some people might have massive amounts of playlists. Passing them around in intents will not work
 * in that case (too many bytes). So instead playlists are kept in here. Dagger will provide a singleton
 * of this. No need to worry about saving state, since widget configuration cannot be returned to if exited.
 */
public class PlaylistsContainer {

    private ArrayList<PlaylistViewModel> mPlaylists;
    private ArrayList<PlaylistViewModel> mSelectedPlaylists;
    private HashSet<String> mSelectedPlaylistIds;

    public PlaylistsContainer() {
        mPlaylists = new ArrayList<>();
        mSelectedPlaylists = new ArrayList<>();
        mSelectedPlaylistIds = new HashSet<>();
    }

    /**
     * Dagger @Singleton injection keeps the instance alive as long as the app is in memory.
     * Data needs to be cleared explicitly just to make sure its gone.
     */
    public void clearAll() {
        mPlaylists.clear();
        mSelectedPlaylists.clear();
        mSelectedPlaylistIds.clear();
    }

    public ArrayList<PlaylistViewModel> getPlaylists() {
        return mPlaylists;
    }

    public ArrayList<PlaylistViewModel> getSelectedPlaylists() {
        return mSelectedPlaylists;
    }

    /**
     * Clears any existing playlists and adds new ones
     */
    public void initializePlaylists(ArrayList<PlaylistViewModel> playlists) {
        mPlaylists.clear();
        addPlaylists(playlists);
    }

    /**
     * Add new playlists after existing ones
     */
    public void addPlaylists(ArrayList<PlaylistViewModel> playlists) {

        // restore selected status
        for (PlaylistViewModel pl : playlists) {
            if (isSelected(pl.id))
                pl.selected = true;
        }

        mPlaylists.addAll(playlists);
    }

    public int getPlaylistsCount() {
        return mPlaylists.size();
    }

    public int getSelectedPlaylistsCount() {
        return mSelectedPlaylistIds.size();
    }

    public void updateSelectedPlaylists() {
        mSelectedPlaylistIds.clear();
        mSelectedPlaylists.clear();
        for (PlaylistViewModel pl : mPlaylists) {
            if (pl.selected) {
                mSelectedPlaylistIds.add(pl.id);
                mSelectedPlaylists.add(pl);
            }

        }
    }

    public boolean isSelected(String id) {
        return mSelectedPlaylistIds.contains(id);
    }
}
