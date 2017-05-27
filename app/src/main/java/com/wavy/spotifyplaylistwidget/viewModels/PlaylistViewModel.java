package com.wavy.spotifyplaylistwidget.viewModels;

import android.os.Parcel;
import android.os.Parcelable;

import com.wavy.spotifyplaylistwidget.network.SpotifyApi;

import java.util.ArrayList;

public class PlaylistViewModel implements Parcelable {

    //TODO: https://source.android.com/source/code-style#follow-field-naming-conventions
    public String name;
    public String id;
    public String imageUrl;
    public int tracks;
    //public String owner;
    public Boolean selected = false;

    public PlaylistViewModel(String name, String id, int tracks, String imageUrl/*, String owner*/) {
        this.name = name;
        this.id = id;
        this.tracks = tracks;
        this.imageUrl = imageUrl;
        //this.owner = owner;
    }

    private PlaylistViewModel(Parcel in) {
        this.name = in.readString();
        this.id = in.readString();
        this.tracks = in.readInt();
        this.imageUrl = in.readString();
        //this.owner = in.readString();
        this.selected = in.readInt() == 1;
    }

    public static ArrayList<PlaylistViewModel> getListForDebug() {

        ArrayList<PlaylistViewModel> playlists = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            playlists.add(new PlaylistViewModel("playlist" + i, "5PFpnK4yLyIlRZW8jEJXir", 100, "todo"));
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
        dest.writeInt(this.tracks);
        dest.writeString(this.imageUrl);
        //dest.writeString(this.owner);
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
