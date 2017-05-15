package com.wavy.spotifyplaylistwidget.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Playlist implements Parcelable {

    public String name;
    public String id;
    public String mImageUrl;
    public int tracks;
    public String owner;
    public Boolean selected = false;

    public Playlist(String name, String id, int tracks, String imageurl, String owner) {
        this.name = name;
        this.id = id;
        this.tracks = tracks;
        mImageUrl = imageurl;
        this.owner = owner;
    }

    private Playlist(Parcel in) {
        this.name = in.readString();
        this.id = in.readString();
        this.tracks = in.readInt();
        mImageUrl = in.readString();
        this.owner = in.readString();
        this.selected = in.readInt() == 1;
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
        dest.writeString(mImageUrl);
        dest.writeString(this.owner);
        dest.writeInt((this.selected) ? 1 : 0);
    }

    public static final Parcelable.Creator<Playlist> CREATOR
            = new Parcelable.Creator<Playlist>() {
        public Playlist createFromParcel(Parcel in) {
            return new Playlist(in);
        }

        public Playlist[] newArray(int size) {
            return new Playlist[size];
        }
    };
}
