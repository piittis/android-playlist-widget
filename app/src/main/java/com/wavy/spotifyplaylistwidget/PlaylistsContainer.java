package com.wavy.spotifyplaylistwidget;

import com.wavy.spotifyplaylistwidget.viewModels.PlaylistViewModel;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Container for user playlists that any activity can use instead on passing stuff in intents or
 * someting like that.
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
    public void clearPlaylists() {
        mPlaylists.clear();
        mSelectedPlaylists.clear();
        mSelectedPlaylistIds.clear();
    }

    public void clearSelectedStatus() {
        mSelectedPlaylistIds.clear();
    }

    public void initializeSelectedStatus(HashSet<String> selectedIds) {
        this.mSelectedPlaylistIds = selectedIds;
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
                mSelectedPlaylists.add(pl);
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

    private boolean isSelected(String id) {
        return mSelectedPlaylistIds.contains(id);
    }
}
