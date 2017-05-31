package com.wavy.spotifyplaylistwidget.viewModels;

import android.os.Parcel;
import android.os.Parcelable;

import com.wavy.spotifyplaylistwidget.network.SpotifyApi;

import java.util.ArrayList;

/**
 * Viewmodel for playlist that is used in the configure activities. Can be parceled.
 */
public class PlaylistViewModel implements Parcelable {

    public String name;
    public String id;
    public String uri;
    public String imageUrl;
    public int tracks;
    public String owner;
    public Boolean selected = false;

    public PlaylistViewModel(String name, String id, String uri, String imageUrl, int tracks, String owner) {
        this.name = name;
        this.id = id;
        this.uri = uri;
        this.imageUrl = imageUrl;
        this.tracks = tracks;
        this.owner = owner;
    }

    private PlaylistViewModel(Parcel in) {
        this.name = in.readString();
        this.id = in.readString();
        this.uri = in.readString();
        this.imageUrl = in.readString();
        this.tracks = in.readInt();
        this.owner = in.readString();
        this.selected = in.readInt() == 1;
    }

    public static ArrayList<PlaylistViewModel> getListForDebug() {

        ArrayList<PlaylistViewModel> playlists = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            playlists.add(new PlaylistViewModel("playlist" + i, "5PFpnK4yLyIlRZW8jEJXir", "uri" , "imageurl", 100, "user1"));
        }
        return playlists;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.id);
        dest.writeString(this.uri);
        dest.writeString(this.imageUrl);
        dest.writeInt(this.tracks);
        dest.writeString(this.owner);
        dest.writeInt((this.selected) ? 1 : 0);
    }

    public static final Creator<PlaylistViewModel> CREATOR
            = new Creator<PlaylistViewModel>() {
        public PlaylistViewModel createFromParcel(Parcel in) {
            return new PlaylistViewModel(in);
        }

        public PlaylistViewModel[] newArray(int size) {
            return new PlaylistViewModel[size];
        }
    };
}
